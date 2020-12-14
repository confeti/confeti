package org.confeti.service;

import org.confeti.db.dao.report.ReportByCompanyDao;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.model.report.ReportByCompanyEntity;
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

import static org.confeti.service.BaseEntityService.findMany;
import static org.confeti.service.BaseEntityService.findOne;
import static org.confeti.service.ReportStatsService.updateReportStatsIf;

@Service
public class ReportService extends AbstractEntityService<ReportEntity, Report, ReportDao> {

    private final ReportByConferenceDao reportByConferenceDao;
    private final ReportBySpeakerDao reportBySpeakerDao;
    private final ReportByTagDao reportByTagDao;
    private final ReportByCompanyDao reportByCompanyDao;
    private final ConferenceService conferenceService;
    private final SpeakerService speakerService;
    private final ReportStatsService reportStatsService;

    protected ReportService(final ReportDao dao,
                            final ReportByConferenceDao reportByConferenceDao,
                            final ReportBySpeakerDao reportBySpeakerDao,
                            final ReportByTagDao reportByTagDao,
                            final ReportByCompanyDao reportByCompanyDao,
                            final ConferenceService conferenceService,
                            final SpeakerService speakerService,
                            final ReportStatsService reportStatsService) {
        super(dao);
        this.reportByConferenceDao = reportByConferenceDao;
        this.reportBySpeakerDao = reportBySpeakerDao;
        this.reportByTagDao = reportByTagDao;
        this.reportByCompanyDao = reportByCompanyDao;
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
                .concatMap(TupleUtils.function(this::spreadReport))
                .then(savedReport.map(ReportEntity::getTags))
                .flatMapMany(Flux::fromIterable)
                .flatMap(tag -> savedReport.zipWith(Mono.just(tag)))
                .flatMap(TupleUtils.function(this::upsertByTag))
                .then(savedReport.map(Report::from));
    }

    @NotNull
    public Mono<Report> upsertByConference(@NotNull final Report report,
                                           @NotNull final String conferenceName,
                                           @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> conferenceService.findBy(conferenceName, year)
                        .map(conf -> ReportByConferenceEntity.from(conf.getName(), conf.getYear(), rep)),
                reportByConferenceDao,
                rep -> Mono.from(reportByConferenceDao.findById(conferenceName, year, rep.getTitle(), rep.getId())));
    }

    @NotNull
    private Mono<Report> upsertByConference(@NotNull final ReportEntity report,
                                            @NotNull final String conferenceName,
                                            @NotNull final Integer year) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> conferenceService.findBy(conferenceName, year)
                        .map(conf -> ReportByConferenceEntity.from(conf.getName(), conf.getYear(), rep)),
                reportByConferenceDao,
                rep -> Mono.from(reportByConferenceDao.findById(conferenceName, year, rep.getTitle(), rep.getId())));
    }

    @NotNull
    public Mono<Report> upsertBySpeaker(@NotNull final Report report,
                                        @NotNull final UUID speakerId,
                                        @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> speakerService.findBy(speakerId)
                        .map(speaker -> ReportBySpeakerEntity.from(speaker.getId(), year, rep)),
                reportBySpeakerDao,
                rep -> Mono.from(reportBySpeakerDao.findById(speakerId, year, rep.getTitle(), rep.getId())));
    }

    @NotNull
    private Mono<Report> upsertBySpeaker(@NotNull final ReportEntity report,
                                         @NotNull final UUID speakerId,
                                         @NotNull final Integer year) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> speakerService.findBy(speakerId)
                        .map(speaker -> ReportBySpeakerEntity.from(speaker.getId(), year, rep)),
                reportBySpeakerDao,
                rep -> Mono.from(reportBySpeakerDao.findById(speakerId, year, rep.getTitle(), rep.getId())));
    }

    @NotNull
    public Mono<Report> upsertByTag(@NotNull final Report report,
                                    @NotNull final String tagName) {
        return upsert(
                upsert(report),
                rep -> Mono.just(ReportByTagEntity.from(tagName, rep)),
                reportByTagDao,
                rep -> Mono.from(reportByTagDao.findById(tagName, rep.getTitle(), rep.getId())));
    }

    @NotNull
    private Mono<Report> upsertByTag(@NotNull final ReportEntity report,
                                     @NotNull final String tagName) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> Mono.just(ReportByTagEntity.from(tagName, rep)),
                reportByTagDao,
                rep -> Mono.from(reportByTagDao.findById(tagName, rep.getTitle(), rep.getId())));
    }

    @NotNull
    public Mono<Report> upsertByCompany(@NotNull final Report report,
                                        @NotNull final String companyName,
                                        @NotNull final Integer year) {
        return upsert(
                upsert(report),
                rep -> Mono.just(ReportByCompanyEntity.from(companyName, year, rep)),
                reportByCompanyDao,
                rep -> Mono.from(reportByCompanyDao.findById(companyName, year, rep.getTitle(), rep.getId())));
    }

    @NotNull
    public Mono<Report> upsertByCompany(@NotNull final ReportEntity report,
                                        @NotNull final String companyName,
                                        @NotNull final Integer year) {
        return upsert(
                upsert(report).map(Report::from),
                rep -> Mono.just(ReportByCompanyEntity.from(companyName, year, rep)),
                reportByCompanyDao,
                rep -> Mono.from(reportByCompanyDao.findById(companyName, year, rep.getTitle(), rep.getId())));
    }

    @NotNull
    private Mono<Report> findByTitleAndCompare(@NotNull final Report report) {
        return findByTitle(report.getTitle())
                .filter(report::compareBySpeakers)
                .collectList()
                .map(filteredReports -> {
                    if (!filteredReports.isEmpty()) {
                        report.setId(filteredReports.get(0).getId());
                    }
                    return report;
                });
    }

    @NotNull
    private Mono<Report> spreadReport(@NotNull final ReportEntity report,
                                      @NotNull final Conference conference) {
        return Flux.fromIterable(report.getSpeakers())
                .map(Speaker::from)
                .flatMap(speaker -> updateReportStatsBySpeaker(conference, speaker, report)
                        .then(Mono.just(speaker)))
                .concatMap(speaker -> updateReportStatsByCompany(speaker, conference.getYear(), report)
                        .flatMap(companyName -> upsertByCompany(report, companyName, conference.getYear()))
                        .then(Mono.just(speaker)))
                .flatMap(speaker -> upsertBySpeaker(report, speaker.getId(), conference.getYear())
                        .then(Mono.just(speaker)))
                .flatMap(speaker -> speakerService.upsert(speaker, conference.getName(), conference.getYear())
                        .then(Mono.just(speaker)))
                .flatMap(speaker -> conferenceService.upsert(conference, speaker.getId()))
                .then(updateReportStatsByConference(conference, report))
                .then(upsertByConference(report, conference.getName(), conference.getYear()));
    }

    @NotNull
    private Mono<Conference> updateReportStatsByConference(@NotNull final Conference conference,
                                                           @NotNull final ReportEntity report) {
        return findByConference(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConfExist -> updateReportStatsIf(
                        !isReportByConfExist,
                        conference,
                        reportStatsService.updateReportStatsByConference(conference.getName(), conference.getYear())));
    }

    @NotNull
    private Mono<Speaker> updateReportStatsBySpeaker(@NotNull final Conference conference,
                                                     @NotNull final Speaker speaker,
                                                     @NotNull final ReportEntity report) {
        return findByConference(conference.getName(), conference.getYear(), report.getTitle(), report.getId())
                .hasElement()
                .flatMap(isReportByConferenceExist -> updateReportStatsIf(
                        !isReportByConferenceExist,
                        speaker,
                        reportStatsService.updateReportStatsBySpeakerForConference(conference.getName(), speaker.getId())))
                .then(findBySpeaker(speaker.getId(), conference.getYear(), report.getTitle(), report.getId()))
                .hasElement()
                .flatMap(isReportBySpeakerExist -> updateReportStatsIf(
                            !isReportBySpeakerExist,
                            speaker,
                            reportStatsService.updateReportStatsBySpeakerForYear(conference.getYear(), speaker.getId())));
    }

    @NotNull
    private Mono<String> updateReportStatsByCompany(@NotNull final Speaker speaker,
                                                    @NotNull final Integer year,
                                                    @NotNull final ReportEntity report) {
        return Mono.justOrEmpty(speaker.getContactInfo())
                .flatMap(contactInfo -> Mono.justOrEmpty(contactInfo.getCompany()))
                .hasElement()
                .flatMap(isCompanyExist -> isCompanyExist
                        ? findByCompany(speaker.getContactInfo().getCompany().getName(), year, report.getTitle(), report.getId())
                        .hasElement()
                        .flatMap(isReportByConfExist -> updateReportStatsIf(
                                !isReportByConfExist,
                                speaker.getContactInfo().getCompany().getName(),
                                reportStatsService.updateReportStatsByCompany(year, speaker.getContactInfo().getCompany().getName())))
                        : Mono.empty());
    }

    @NotNull
    public Flux<Report> findByTitle(@NotNull final UUID id,
                                    @NotNull final String title) {
        return findMany(dao.findByTitle(id, title), Report::from);
    }

    @NotNull
    public Flux<Report> findByTitle(@NotNull final String title) {
        return findMany(dao.findByTitle(title), Report::from);
    }

    @NotNull
    public Mono<Report> findById(@NotNull final UUID id) {
        return findOne(dao.findById(id), Report::from);
    }

    @NotNull
    public Flux<Report> findBySpeaker(@NotNull final UUID speakerId) {
        return findMany(
                reportBySpeakerDao.findBySpeakerId(speakerId),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBySpeaker(@NotNull final UUID speakerId,
                                      @NotNull final Integer year) {
        return findMany(
                reportBySpeakerDao.findBySpeakerIdForYear(speakerId, year),
                Report::from);
    }

    @NotNull
    public Flux<Report> findBySpeaker(@NotNull final UUID speakerId,
                                      @NotNull final Integer year,
                                      @NotNull final String title) {
        return findMany(
                reportBySpeakerDao.findByTitle(speakerId, year, title),
                Report::from);
    }

    @NotNull
    public Mono<Report> findBySpeaker(@NotNull final UUID speakerId,
                                      @NotNull final Integer year, 
                                      @NotNull final String title, 
                                      @NotNull final UUID id) {
        return findOne(
                reportBySpeakerDao.findById(speakerId, year, title, id),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByTag(@NotNull final String tagName) {
        return findMany(
                reportByTagDao.findByTagName(tagName),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByTag(@NotNull final String tagName,
                                  @NotNull final String title) {
        return findMany(
                reportByTagDao.findByTitle(tagName, title),
                Report::from);
    }

    @NotNull
    public Mono<Report> findByTag(@NotNull final String tagName, 
                                  @NotNull final String title, 
                                  @NotNull final UUID id) {
        return findOne(
                reportByTagDao.findById(tagName, title, id),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByCompany(@NotNull final String companyName) {
        return findMany(
                reportByCompanyDao.findByCompanyName(companyName),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByCompany(@NotNull final String companyName,
                                      @NotNull final Integer year) {
        return findMany(
                reportByCompanyDao.findByCompanyNameForYear(companyName, year),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByCompany(@NotNull final String companyName,
                                      @NotNull final Integer year,
                                      @NotNull final String title) {
        return findMany(
                reportByCompanyDao.findByTitle(companyName, year, title),
                Report::from);
    }

    @NotNull
    public Mono<Report> findByCompany(@NotNull final String companyName,
                                      @NotNull final Integer year,
                                      @NotNull final String title,
                                      @NotNull final UUID id) {
        return findOne(
                reportByCompanyDao.findById(companyName, year, title, id),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByConference(@NotNull final String conferenceName) {
        return findMany(
                reportByConferenceDao.findByConferenceName(conferenceName),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByConference(@NotNull final String conferenceName,
                                         @NotNull final Integer year) {
        return findMany(
                reportByConferenceDao.findByConferenceNameForYear(conferenceName, year),
                Report::from);
    }

    @NotNull
    public Flux<Report> findByConference(@NotNull final String conferenceName,
                                         @NotNull final Integer year,
                                         @NotNull final String title) {
        return findMany(
                reportByConferenceDao.findByTitle(conferenceName, year, title),
                Report::from);
    }

    @NotNull
    public Mono<Report> findByConference(@NotNull final String conferenceName,
                                         @NotNull final Integer year,
                                         @NotNull final String title,
                                         @NotNull final UUID id) {
        return findOne(
                reportByConferenceDao.findById(conferenceName, year, title, id),
                Report::from);
    }

    @NotNull
    public Flux<Report> findAll() {
        return findMany(dao.findAll(), Report::from);
    }

    @NotNull
    @Override
    protected Mono<ReportEntity> findByPrimaryKey(@NotNull final Report report) {
        return Mono.from(dao.findById(report.getId()));
    }
}
