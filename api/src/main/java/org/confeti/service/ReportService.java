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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

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
        final var savedReport = upsert(report, ReportEntity::from).cache();
        return Mono.just(report)
                .map(Report::getConferences)
                .flatMapMany(Flux::fromIterable)
                .flatMap(conference -> savedReport.zipWith(Mono.just(conference)))
                .flatMap(TupleUtils.function(this::spreadReport))
                .then(savedReport.map(ReportEntity::getTags))
                .flatMapMany(Flux::fromIterable)
                .flatMap(tag -> savedReport.zipWith(Mono.just(tag)))
                .flatMap(TupleUtils.function((BiFunction<ReportEntity, String, Mono<Report>>) this::upsert))
                .then(savedReport.map(Report::from));
    }

    @NotNull
    public Mono<Report> upsert(@NotNull final Report report,
                               @NotNull final String conferenceName,
                               @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> conferenceService.findBy(conferenceName, year)
                        .map(conf -> ReportByConferenceEntity.from(conf.getName(), conf.getYear(), rep)),
                reportByConferenceDao);
    }

    @NotNull
    private Mono<Report> upsert(@NotNull final ReportEntity report,
                                @NotNull final String conferenceName,
                                @NotNull final Integer year) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> conferenceService.findBy(conferenceName, year)
                        .map(conf -> ReportByConferenceEntity.from(conf.getName(), conf.getYear(), rep)),
                reportByConferenceDao);
    }

    @NotNull
    public Mono<Report> upsert(@NotNull final Report report,
                               @NotNull final UUID speakerId,
                               @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> speakerService.findBy(speakerId)
                        .map(speaker -> ReportBySpeakerEntity.from(speaker.getId(), year, rep)),
                reportBySpeakerDao);
    }

    @NotNull
    private Mono<Report> upsert(@NotNull final ReportEntity report,
                                @NotNull final UUID speakerId,
                                @NotNull final Integer year) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> speakerService.findBy(speakerId)
                        .map(speaker -> ReportBySpeakerEntity.from(speaker.getId(), year, rep)),
                reportBySpeakerDao);
    }

    @NotNull
    public Mono<Report> upsert(@NotNull final Report report,
                               @NotNull final String tagName) {
        return upsert(
                upsert(report),
                conf -> Mono.just(ReportByTagEntity.from(tagName, report)),
                reportByTagDao);
    }

    @NotNull
    private Mono<Report> upsert(@NotNull final ReportEntity report,
                                @NotNull final String tagName) {
        return upsert(
                upsert(report).map(Report::from),
                conf -> Mono.just(ReportByTagEntity.from(tagName, report)),
                reportByTagDao);
    }

    @NotNull
    private Flux<?> spreadReport(@NotNull final ReportEntity report,
                                 @NotNull final Conference conference) {
        return conferenceService.upsert(conference)
                .flatMap(conf -> updateReportStats(conference, report))
                .flatMap(conf -> upsert(report, conf.getName(), conf.getYear()).then(Mono.just(conf)))
                .then(Mono.just(report.getSpeakers()))
                .flatMapMany(Flux::fromIterable)
                .map(Speaker::from)
                .flatMap(speaker -> updateReportStats(conference, speaker, report))
                .flatMap(speaker -> speakerService.upsert(speaker, conference.getName(), conference.getYear()))
                .flatMap(speaker -> upsert(report, speaker.getId(), conference.getYear()))
                .flatMap(speaker -> conferenceService.upsert(conference, speaker.getId()));
    }

    @NotNull
    private <T> Mono<T> updateReportStats(boolean isReportExist,
                                          @NotNull final T entityThatReturn,
                                          @NotNull final Supplier<Mono<?>> incrementReportTotal) {
        if (!isReportExist) {
            return Mono.from(incrementReportTotal.get())
                    .then(Mono.just(entityThatReturn));
        }
        return Mono.just(entityThatReturn);
    }

    @NotNull
    private Mono<Conference> updateReportStats(@NotNull final Conference conference,
                                               @NotNull final ReportEntity report) {
        return findBy(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConfExist -> updateReportStats(
                        isReportByConfExist,
                        conference,
                        () -> Mono.from(reportStatsByConferenceDao.incrementReportTotal(
                                conference.getName(), conference.getYear(), 1L))));
    }

    @NotNull
    private Mono<Speaker> updateReportStats(@NotNull final Conference conference,
                                            @NotNull final Speaker speaker,
                                            @NotNull final ReportEntity report) {
        return findBy(speaker.getId(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportBySpeakerExist -> updateReportStats(
                        isReportBySpeakerExist,
                        speaker,
                        () -> Mono.from(reportStatsBySpeakerDao.incrementReportTotal(
                                    speaker.getId(), conference.getYear(), conference.getName(), 1L))
                                .then(Mono.from(reportStatsByCompanyDao.incrementReportTotal(
                                                speaker.getContactInfo().getCompany().getName(), conference.getYear(), 1L)))));
    }

    @NotNull
    public Mono<Report> findBy(@NotNull final UUID id) {
        return findOneBy(dao.findById(id), Report::from);
    }

    @NotNull
    public Mono<Report> findBy(@NotNull final String conferenceName,
                               @NotNull final Integer year,
                               @NotNull final String title,
                               @NotNull final UUID id) {
        return findOneBy(
                reportByConferenceDao.findById(conferenceName, year, title, id),
                Report::from);
    }

    @NotNull
    public Mono<Report> findBy(@NotNull final UUID speakerId,
                               @NotNull final Integer year,
                               @NotNull final String title,
                               @NotNull final UUID id) {
        return findOneBy(
                reportBySpeakerDao.findById(speakerId, year, title, id),
                Report::from);
    }

    @NotNull
    public Mono<Report> findBy(@NotNull final String tagName,
                               @NotNull final String title,
                               @NotNull final UUID id) {
        return findOneBy(
                reportByTagDao.findById(tagName, title, id),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final String conferenceName,
                               @NotNull final Integer year,
                               @NotNull final String title) {
        return findAllBy(
                reportByConferenceDao.findByTitle(conferenceName, year, title),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final UUID speakerId,
                               @NotNull final Integer year,
                               @NotNull final String title) {
        return findAllBy(
                reportBySpeakerDao.findByTitle(speakerId, year, title),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBy(@NotNull final String tagName,
                               @NotNull final String title) {
        return findAllBy(
                reportByTagDao.findByTitle(tagName, title),
                Report::from);
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
