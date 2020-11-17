package org.confeti.service;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
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

import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.confeti.service.BaseEntityService.findAllBy;
import static org.confeti.service.BaseEntityService.findOneBy;
import static org.confeti.service.ReportStatsService.updateReportStatsIf;

@Service
public final class ReportService extends AbstractEntityService<ReportEntity, Report, ReportDao> {

    private final ReportByConferenceDao reportByConferenceDao;
    private final ReportBySpeakerDao reportBySpeakerDao;
    private final ReportByTagDao reportByTagDao;
    private final ConferenceService conferenceService;
    private final SpeakerService speakerService;
    private final ReportStatsService reportStatsService;

    protected ReportService(final ReportDao dao,
                            final ReportByConferenceDao reportByConferenceDao,
                            final ReportBySpeakerDao reportBySpeakerDao,
                            final ReportByTagDao reportByTagDao,
                            final ConferenceService conferenceService,
                            final SpeakerService speakerService,
                            final ReportStatsService reportStatsService) {
        super(dao);
        this.reportByConferenceDao = reportByConferenceDao;
        this.reportBySpeakerDao = reportBySpeakerDao;
        this.reportByTagDao = reportByTagDao;
        this.conferenceService = conferenceService;
        this.speakerService = speakerService;
        this.reportStatsService = reportStatsService;
    }

    @NotNull
    @Override
    public Mono<Report> upsert(@NotNull final Report report) {
        final var newReport = Report.from(report);
        if (newReport.getId() == null) {
            newReport.setId(UUID.randomUUID());
        }

        final var companies = newReport.getSpeakers().stream()
                .map(Speaker::getContactInfo)
                .map(Speaker.ContactInfo::getCompany)
                .map(Speaker.ContactInfo.SpeakerCompany::getName)
                .collect(Collectors.toSet());

        final var savedReport = upsertMany(newReport.getConferences(), conferenceService::upsert)
                .doOnNext(newReport::setConferences)
                .then(upsertMany(newReport.getSpeakers(), speakerService::upsert))
                .doOnNext(newReport::setSpeakers)
                .then(Mono.just(newReport))
                .flatMap(this::findByTitleAndCompare)
                .flatMap(rep -> upsert(rep, ReportEntity::from)).cache();

        return Mono.just(newReport)
                .map(Report::getConferences)
                .flatMapMany(Flux::fromIterable)
                .flatMap(conference -> savedReport.zipWith(Mono.just(conference)))
                .concatMap(TupleUtils.function((reportEntity, conference) ->
                        spreadReport(reportEntity, conference, Sets.newConcurrentHashSet(companies))))
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
                rep -> Mono.just(ReportByTagEntity.from(tagName, rep)),
                reportByTagDao);
    }

    @NotNull
    private Mono<Report> upsert(@NotNull final ReportEntity report,
                                @NotNull final String tagName) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> Mono.just(ReportByTagEntity.from(tagName, rep)),
                reportByTagDao);
    }

    @NotNull
    private Mono<Report> findByTitleAndCompare(@NotNull final Report report) {
        return findByTitle(report.getTitle())
                .filter(report::compareBySpeakers)
                .collectList()
                .map(foundSpeakers -> {
                    if (!foundSpeakers.isEmpty()) {
                        report.setId(foundSpeakers.get(0).getId());
                    }
                    return report;
                });
    }

    @NotNull
    private Mono<?> spreadReport(@NotNull final ReportEntity report,
                                 @NotNull final Conference conference,
                                 @NotNull final Set<String> companies) {
        return Flux.fromIterable(report.getSpeakers())
                .map(Speaker::from)
                .flatMap(speaker -> updateReportStatsBySpeaker(conference, speaker, report, companies)
                        .then(Mono.just(speaker)))
                .flatMap(speaker -> upsert(report, speaker.getId(), conference.getYear())
                        .then(Mono.just(speaker)))
                .flatMap(speaker -> speakerService.upsert(speaker, conference.getName(), conference.getYear())
                        .then(Mono.just(speaker)))
                .flatMap(speaker -> conferenceService.upsert(conference, speaker.getId()))
                .then(updateReportStatsByConference(conference, report))
                .then(upsert(report, conference.getName(), conference.getYear()));
    }

    @NotNull
    private Mono<Conference> updateReportStatsByConference(@NotNull final Conference conference,
                                                           @NotNull final ReportEntity report) {
        return findBy(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConfExist -> updateReportStatsIf(
                        !isReportByConfExist,
                        conference,
                        reportStatsService.updateReportStatsByConference(conference.getName(), conference.getYear())));
    }

    @NotNull
    private Mono<Speaker> updateReportStatsBySpeaker(@NotNull final Conference conference,
                                                     @NotNull final Speaker speaker,
                                                     @NotNull final ReportEntity report,
                                                     @NotNull final Set<String> companies) {
        return findBy(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConferenceExist -> updateReportStatsIf(
                        !isReportByConferenceExist,
                        speaker,
                        reportStatsService.updateReportStatsBySpeakerForConference(conference.getName(), speaker.getId())))
                .then(findBy(speaker.getId(), conference.getYear(), report.getTitle(), report.getId()))
                .hasElement()
                .flatMap(isReportBySpeakerExist -> updateReportStatsIf(
                            !isReportBySpeakerExist,
                            speaker,
                            reportStatsService.updateReportStatsBySpeakerForYear(conference.getYear(), speaker.getId())
                                    .then(updateReportStatsByCompanies(conference, companies))));
    }

    @NotNull
    private Mono<Conference> updateReportStatsByCompanies(@NotNull final Conference conference,
                                                          @NotNull final Set<String> companies) {
        return Flux.fromIterable(companies)
                .flatMap(company -> {
                    companies.remove(company);
                    return reportStatsService.updateReportStatsByCompany(conference.getYear(), company);
                })
                .then(Mono.just(conference));
    }

    @NotNull
    public Flux<Report> findByTitle(@NotNull final String title) {
        return findAllBy(dao.findByTitle(title), Report::from);
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
    protected Mono<ReportEntity> findByPrimaryKey(@NotNull final Report report) {
        return Mono.from(dao.findById(report.getId()));
    }
}
