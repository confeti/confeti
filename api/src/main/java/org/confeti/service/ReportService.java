package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerDao;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.exception.NotFoundException;
import org.confeti.handlers.dto.Conference;
import org.confeti.handlers.dto.Report;
import org.confeti.handlers.dto.Speaker;
import org.confeti.util.converter.DtoToModelConverter;
import org.confeti.util.converter.ModelToDtoConverter;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.UUID;

import static org.confeti.exception.NotFoundException.Entity;

@Service
public final class ReportService extends AbstractEntityService<ReportEntity, Report, ReportDao> {

    private final ReportByConferenceDao reportByConferenceDao;
    private final ReportBySpeakerDao reportBySpeakerDao;
    private final ReportByTagDao reportByTagDao;
    private final ReportStatsByConferenceDao reportStatsByConferenceDao;
    private final ReportStatsBySpeakerDao reportStatsBySpeakerDao;
    private final ReportStatsByCompanyDao reportStatsByCompanyDao;
    private final ConferenceService conferenceService;
    private final SpeakerService speakerService;

    protected ReportService(final ReportDao dao,
                            final ReportByConferenceDao reportByConferenceDao,
                            final ReportBySpeakerDao reportBySpeakerDao,
                            final ReportByTagDao reportByTagDao,
                            final ReportStatsByConferenceDao reportStatsByConferenceDao,
                            final ReportStatsBySpeakerDao reportStatsBySpeakerDao,
                            final ReportStatsByCompanyDao reportStatsByCompanyDao,
                            final ConferenceService conferenceService,
                            final SpeakerService speakerService) {
        super(dao);
        this.reportByConferenceDao = reportByConferenceDao;
        this.reportBySpeakerDao = reportBySpeakerDao;
        this.reportByTagDao = reportByTagDao;
        this.reportStatsByConferenceDao = reportStatsByConferenceDao;
        this.reportStatsBySpeakerDao = reportStatsBySpeakerDao;
        this.reportStatsByCompanyDao = reportStatsByCompanyDao;
        this.conferenceService = conferenceService;
        this.speakerService = speakerService;
    }

    @NotNull
    @Override
    public Mono<Report> upsert(@NotNull final Report report) {
        if (report.getId() == null) {
            report.setId(UUID.randomUUID());
        }

        final var savedReport = upsert(report, DtoToModelConverter::convert, ModelToDtoConverter::convert);
        return savedReport.map(Report::getConferences)
                .flatMapMany(Flux::fromIterable)
                .zipWith(savedReport)
                .flatMap(TupleUtils.function(this::spreadReport))
                .then(savedReport.map(Report::getTags))
                .flatMapMany(Flux::fromIterable)
                .flatMap(tagName -> upsert(report, tagName))
                .then(savedReport);

    }

    @NotNull
    Mono<Report> upsert(@NotNull final Report report,
                        @NotNull final String conferenceName,
                        @NotNull final Integer year) {
        return upsert(
                upsert(report, DtoToModelConverter::convert, ModelToDtoConverter::convert),
                rep -> conferenceService.findBy(conferenceName, year)
                        .then(Mono.just(DtoToModelConverter.convert(rep, conferenceName, year))),
                reportByConferenceDao,
                ModelToDtoConverter::convert);
    }

    @NotNull
    Mono<Report> upsert(@NotNull final Report report,
                        @NotNull final UUID speakerId,
                        @NotNull final Integer year) {
        return upsert(
                upsert(report, DtoToModelConverter::convert, ModelToDtoConverter::convert),
                rep -> speakerService.findBy(speakerId)
                        .then(Mono.just(DtoToModelConverter.convert(rep, speakerId, year))),
                reportBySpeakerDao,
                ModelToDtoConverter::convert);
    }

    @NotNull
    Mono<Report> upsert(@NotNull final Report report,
                        @NotNull final String tagName) {
        return upsert(
                upsert(report, DtoToModelConverter::convert, ModelToDtoConverter::convert),
                conf -> Mono.just(DtoToModelConverter.convert(report, tagName)),
                reportByTagDao,
                ModelToDtoConverter::convert);
    }

    @NotNull
    private Publisher<?> spreadReport(@NotNull final Conference conference,
                                      @NotNull final Report report) {
        return conferenceService.upsert(conference)
                .flatMap(conf -> upsert(report, conf.getName(), conf.getYear()).then(Mono.just(conf)))
                .flatMap(this::updateReportStats)
                .flatMapMany(conf -> Flux.fromIterable(report.getSpeakers())
                        .flatMap(speaker -> speakerService.upsert(speaker, conference.getName(), conference.getYear()))
                        .flatMap(speaker -> updateReportStats(conf, speaker))
                        .flatMap(speaker -> upsert(report, speaker.getId(), conf.getYear()))
                        .flatMap(speaker -> conferenceService.upsert(conf, speaker.getId())));
    }

    @NotNull
    private Mono<Conference> updateReportStats(@NotNull final Conference conference) {
        return Mono.from(reportStatsByConferenceDao.incrementReportTotal(conference.getName(), conference.getYear(), 1L))
                .map(rr -> conference);
    }

    @NotNull
    private Mono<Speaker> updateReportStats(@NotNull final Conference conference,
                                            @NotNull final Speaker speaker) {
        return Mono.from(
                    reportStatsBySpeakerDao.incrementReportTotal(speaker.getId(), conference.getYear(), conference.getName(), 1L))
                .then(Mono.from(
                        reportStatsByCompanyDao.incrementReportTotal(speaker.getContactInfo().getCompany(), conference.getYear(), 1L)))
                .map(rr -> speaker);
    }

    @NotNull
    @Override
    protected MappedReactiveResultSet<ReportEntity> findByPrimaryKey(@NotNull final Report report) {
        return dao.findById(report.getId());
    }

    @NotNull
    public Mono<Report> findBy(@NotNull final UUID id) {
        return findBy(
                dao.findById(id),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.REPORT, id.toString()));
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final String conferenceName) {
        return findBy(
                reportByConferenceDao.findByConferenceName(conferenceName),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.REPORT, conferenceName)).flux();
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final String conferenceName,
                               @NotNull final Integer year) {
        return findBy(
                reportByConferenceDao.findByConferenceNameForYear(conferenceName, year),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.REPORT, String.format("%s:%d", conferenceName, year))).flux();
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final UUID speakerId,
                               @NotNull final Integer year) {
        return findBy(
                reportBySpeakerDao.findBySpeakerIdForYear(speakerId, year),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.CONFERENCE, String.format("%s:%d", speakerId.toString(), year))).flux();
    }

    @NotNull
    public Flux<Report> findBySpeakerId(@NotNull final UUID speakerId) {
        return findBy(
                reportBySpeakerDao.findBySpeakerId(speakerId),
                ModelToDtoConverter::convert,
                new NotFoundException(Entity.CONFERENCE, speakerId.toString())).flux();
    }
}
