package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerDao;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.UUID;

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
        final var reportEntity = ReportEntity.from(report);
        final var savedEntity = Mono.from(findByPrimaryKey(report))
                .doOnNext(re -> re.updateFrom(reportEntity))
                .defaultIfEmpty(reportEntity)
                .flatMap(this::upsert)
                .cache();
        return Mono.just(report)
                .map(Report::getConferences)
                .flatMapMany(Flux::fromIterable)
                .zipWith(savedEntity)
                .flatMap(TupleUtils.function(this::spreadReport))
                .then(savedEntity.map(ReportEntity::getTags))
                .flatMapMany(Flux::fromIterable)
                .flatMap(tagName -> upsert(report, tagName))
                .then(savedEntity.map(Report::from));
    }

    @NotNull
    public Mono<Report> upsert(@NotNull final Report report,
                               @NotNull final String conferenceName,
                               @NotNull final Integer year) {
        return upsert(ReportEntity.from(report), conferenceName, year)
                .map(Report::from);
    }

    @NotNull
    private Mono<ReportEntity> upsert(@NotNull final ReportEntity report,
                                      @NotNull final String conferenceName,
                                      @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> conferenceService.findBy(conferenceName, year)
                        .map(conf -> ReportByConferenceEntity.from(conf.getName(), conf.getYear(), rep)),
                reportByConferenceDao);
    }

    @NotNull
    public Mono<Report> upsert(@NotNull final Report report,
                               @NotNull final UUID speakerId,
                               @NotNull final Integer year) {
        return upsert(ReportEntity.from(report), speakerId, year)
                .map(Report::from);
    }

    @NotNull
    private Mono<ReportEntity> upsert(@NotNull final ReportEntity report,
                                      @NotNull final UUID speakerId,
                                      @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> speakerService.findBy(speakerId)
                        .map(speaker -> ReportBySpeakerEntity.from(speaker.getId(), year, rep)),
                reportBySpeakerDao);
    }

    @NotNull
    public Mono<Report> upsert(@NotNull final Report report,
                               @NotNull final String tagName) {
        return upsert(ReportEntity.from(report), tagName)
                .map(Report::from);
    }

    @NotNull
    private Mono<ReportEntity> upsert(@NotNull final ReportEntity report,
                                      @NotNull final String tagName) {
        return upsert(
                upsert(report),
                conf -> Mono.just(ReportByTagEntity.from(tagName, report)),
                reportByTagDao);
    }

    @NotNull
    private Publisher<?> spreadReport(@NotNull final Conference conference,
                                      @NotNull final ReportEntity report) {
        return conferenceService.upsert(conference)
                .flatMap(conf -> upsert(report, conf.getName(), conf.getYear()).then(Mono.just(conf)))
                .flatMap(this::updateReportStats)
                .flatMapMany(conf -> Flux.fromIterable(report.getSpeakers())
                        .map(Speaker::from)
                        .flatMap(speaker -> speakerService.upsert(speaker, conference.getName(), conference.getYear()))
                        .flatMap(speaker -> updateReportStats(conf, speaker))
                        .flatMap(speaker -> upsert(report, speaker.getId(), conf.getYear()))
                        .flatMap(speaker -> conferenceService.upsert(conf, speaker.getId())));
    }

    @NotNull
    private Mono<Conference> updateReportStats(@NotNull final Conference conference) {
        return Mono.from(reportStatsByConferenceDao.incrementReportTotal(conference.getName(), conference.getYear(), 1L))
                .then(Mono.just(conference));
    }

    @NotNull
    private Mono<Speaker> updateReportStats(@NotNull final Conference conference,
                                            @NotNull final Speaker speaker) {
        return Mono.from(
                    reportStatsBySpeakerDao.incrementReportTotal(
                            speaker.getId(), conference.getYear(), conference.getName(), 1L))
                .then(Mono.from(
                        reportStatsByCompanyDao.incrementReportTotal(
                                speaker.getContactInfo().getCompany().getName(), conference.getYear(), 1L)))
                .then(Mono.just(speaker));
    }

    @NotNull
    public Mono<Report> findBy(@NotNull final UUID id) {
        return findOneBy(dao.findById(id), Report::from);
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final String conferenceName) {
        return findAllBy(
                reportByConferenceDao.findByConferenceName(conferenceName),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final String conferenceName,
                               @NotNull final Integer year) {
        return findAllBy(
                reportByConferenceDao.findByConferenceNameForYear(conferenceName, year),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final UUID speakerId,
                               @NotNull final Integer year) {
        return findAllBy(
                reportBySpeakerDao.findBySpeakerIdForYear(speakerId, year),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBySpeakerId(@NotNull final UUID speakerId) {
        return findAllBy(
                reportBySpeakerDao.findBySpeakerId(speakerId),
                Report::from);
    }

    @NotNull
    @Override
    protected MappedReactiveResultSet<ReportEntity> findByPrimaryKey(@NotNull final Report report) {
        return dao.findById(report.getId());
    }
}
