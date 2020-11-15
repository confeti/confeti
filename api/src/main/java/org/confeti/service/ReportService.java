package org.confeti.service;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForYearDao;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.confeti.service.dto.ReportStats;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public final class ReportService extends AbstractEntityService<ReportEntity, Report, ReportDao> {

    private final ReportByConferenceDao reportByConferenceDao;
    private final ReportBySpeakerDao reportBySpeakerDao;
    private final ReportByTagDao reportByTagDao;
    private final ReportStatsByConferenceDao reportStatsByConferenceDao;
    private final ReportStatsBySpeakerForYearDao reportStatsBySpeakerForYearDao;
    private final ReportStatsBySpeakerForConferenceDao reportStatsBySpeakerForConferenceDao;
    private final ReportStatsByCompanyDao reportStatsByCompanyDao;
    private final ConferenceService conferenceService;
    private final SpeakerService speakerService;

    protected ReportService(final ReportDao dao,
                            final ReportByConferenceDao reportByConferenceDao,
                            final ReportBySpeakerDao reportBySpeakerDao,
                            final ReportByTagDao reportByTagDao,
                            final ReportStatsByConferenceDao reportStatsByConferenceDao,
                            final ReportStatsBySpeakerForYearDao reportStatsBySpeakerForYearDao,
                            final ReportStatsBySpeakerForConferenceDao reportStatsBySpeakerForConferenceDao,
                            final ReportStatsByCompanyDao reportStatsByCompanyDao,
                            final ConferenceService conferenceService,
                            final SpeakerService speakerService) {
        super(dao);
        this.reportByConferenceDao = reportByConferenceDao;
        this.reportBySpeakerDao = reportBySpeakerDao;
        this.reportByTagDao = reportByTagDao;
        this.reportStatsByConferenceDao = reportStatsByConferenceDao;
        this.reportStatsBySpeakerForYearDao = reportStatsBySpeakerForYearDao;
        this.reportStatsBySpeakerForConferenceDao = reportStatsBySpeakerForConferenceDao;
        this.reportStatsByCompanyDao = reportStatsByCompanyDao;
        this.conferenceService = conferenceService;
        this.speakerService = speakerService;
    }

    @NotNull
    @Override
    public Mono<Report> upsert(@NotNull final Report report) {
        final var newReport = Report.from(report);
        if (newReport.getId() == null) {
            newReport.setId(UUID.randomUUID());
        }

        final var uniqueCompanies = newReport.getSpeakers().stream()
                .map(Speaker::getContactInfo)
                .map(Speaker.ContactInfo::getCompany)
                .map(Speaker.ContactInfo.SpeakerCompany::getName)
                .collect(Collectors.toSet());

        final var savedReport = upsertConferences(newReport)
                .flatMap(this::upsertSpeakers)
                .flatMap(this::findByTitleAndCompare)
                .flatMap(rep -> upsert(rep, ReportEntity::from)).cache();

        return Mono.just(newReport)
                .map(Report::getConferences)
                .flatMapMany(Flux::fromIterable)
                .flatMap(conference -> savedReport.zipWith(Mono.just(conference)))
                .concatMap(TupleUtils.function((reportEntity, conference) ->
                        spreadReport(reportEntity, conference, Sets.newConcurrentHashSet(uniqueCompanies))))
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
    private Mono<Report> upsertConferences(@NotNull final Report report) {
        return upsertDependencies(report, Report::getConferences, conferenceService::upsert)
                .map(conferences -> {
                    report.setConferences(null);
                    report.setConferences(conferences);
                    return report;
                });
    }

    @NotNull
    private Mono<Report> upsertSpeakers(@NotNull final Report report) {
        return upsertDependencies(report, Report::getSpeakers, speakerService::upsert)
                .map(speakers -> {
                    report.setSpeakers(null);
                    report.setSpeakers(speakers);
                    return report;
                });
    }

    @NotNull
    private <T> Mono<Set<T>> upsertDependencies(@NotNull final Report report,
                                                @NotNull final Function<Report, Set<T>> getDependencies,
                                                @NotNull final Function<T, Mono<T>> upsert) {
        return Mono.just(report)
                .map(getDependencies)
                .flatMapMany(Flux::fromIterable)
                .flatMap(upsert)
                .collectList()
                .map(Sets::newHashSet);
    }

    @NotNull
    private Mono<?> spreadReport(@NotNull final ReportEntity report,
                                 @NotNull final Conference conference,
                                 @NotNull final Set<String> uniqueCompanies) {
        return Flux.fromIterable(report.getSpeakers())
                .map(Speaker::from)
                .flatMap(speaker -> updateReportStatsBySpeaker(conference, speaker, report, uniqueCompanies)
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
    private <T> Mono<T> updateReportStatsIf(boolean needToIncrement,
                                            @NotNull final T entityThatReturn,
                                            @NotNull final Mono<?> incrementReportTotal) {
        if (needToIncrement) {
            return incrementReportTotal.then(Mono.just(entityThatReturn));
        }
        return Mono.just(entityThatReturn);
    }

    @NotNull
    private Mono<Conference> updateReportStatsByConference(@NotNull final Conference conference,
                                                           @NotNull final ReportEntity report) {
        return findBy(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConfExist -> updateReportStatsIf(
                        !isReportByConfExist,
                        conference,
                        Mono.from(reportStatsByConferenceDao.incrementReportTotal(
                                conference.getName(), conference.getYear(), 1L))));
    }

    @NotNull
    private Mono<Speaker> updateReportStatsBySpeaker(@NotNull final Conference conference,
                                                     @NotNull final Speaker speaker,
                                                     @NotNull final ReportEntity report,
                                                     @NotNull final Set<String> uniqueCompanies) {
        return findBy(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConferenceExist -> updateReportStatsIf(
                        !isReportByConferenceExist,
                        speaker,
                        updateReportStatsBySpeakerForConference(conference, speaker)))
                .then(findBy(speaker.getId(), conference.getYear(), report.getTitle(), report.getId()))
                .hasElement()
                .flatMap(isReportBySpeakerExist -> updateReportStatsIf(
                            !isReportBySpeakerExist,
                            speaker,
                            updateReportStatsBySpeakerForYear(conference, speaker)
                                    .then(updateReportStatsByCompanies(conference, uniqueCompanies))));
    }

    @NotNull
    private Mono<Speaker> updateReportStatsBySpeakerForConference(@NotNull final Conference conference,
                                                                  @NotNull final Speaker speaker) {
        return Mono.from(reportStatsBySpeakerForConferenceDao.incrementReportTotal(
                                speaker.getId(), conference.getName(), 1L))
                .then(Mono.just(speaker));
    }

    @NotNull
    private Mono<Speaker> updateReportStatsBySpeakerForYear(@NotNull final Conference conference,
                                                            @NotNull final Speaker speaker) {
        return Mono.from(reportStatsBySpeakerForYearDao.incrementReportTotal(
                speaker.getId(), conference.getYear(), 1L))
                .then(Mono.just(speaker));
    }

    @NotNull
    private Mono<Conference> updateReportStatsByCompanies(@NotNull final Conference conference,
                                                          @NotNull final Set<String> companies) {
        return Mono.just(companies)
                .flatMapMany(Flux::fromIterable)
                .flatMap(company -> {
                    companies.remove(company);
                    return Mono.from(reportStatsByCompanyDao.incrementReportTotal(company, conference.getYear(), 1L));
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
    public Flux<ReportStats> countConferenceStats(@NotNull final String conferenceName) {
        return findAllBy(
                reportStatsByConferenceDao.findByConferenceName(conferenceName),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countConferenceStatsForYear(@NotNull final String conferenceName,
                                                         @NotNull final Integer year) {
        return findOneBy(
                reportStatsByConferenceDao.findByConferenceNameByYear(conferenceName, year),
                ReportStats::from);
    }

    @NotNull
    public Flux<ReportStats> countSpeakerStatsForYears(@NotNull final UUID speakerId) {
        return findAllBy(
                reportStatsBySpeakerForYearDao.findBySpeakerId(speakerId),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countSpeakerStatsForYear(@NotNull final UUID speakerId,
                                                      @NotNull final Integer year) {
        return findOneBy(
                reportStatsBySpeakerForYearDao.findBySpeakerIdForYear(speakerId, year),
                ReportStats::from);
    }

    @NotNull
    public Flux<ReportStats> countSpeakerStatsForConferences(@NotNull final UUID speakerId) {
        return findAllBy(
                reportStatsBySpeakerForConferenceDao.findBySpeakerId(speakerId),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countSpeakerStatsForConference(@NotNull final UUID speakerId,
                                                            @NotNull final String conferenceName) {
        return findOneBy(
                reportStatsBySpeakerForConferenceDao.findBySpeakerIdForConference(speakerId, conferenceName),
                ReportStats::from);
    }

    @NotNull
    public Flux<ReportStats> countCompanyStats(@NotNull final String companyName) {
        return findAllBy(
                reportStatsByCompanyDao.findByCompanyName(companyName),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countCompanyStatsForYear(@NotNull final String companyName,
                                                      @NotNull final Integer year) {
        return findOneBy(
                reportStatsByCompanyDao.findByCompanyNameForYear(companyName, year),
                ReportStats::from);
    }

    @NotNull
    @Override
    protected Mono<ReportEntity> findByPrimaryKey(@NotNull final Report report) {
        return Mono.from(dao.findById(report.getId()));
    }
}
