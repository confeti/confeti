package org.confeti.service;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForYearDao;
import org.confeti.service.dto.ReportStats;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.confeti.service.BaseEntityService.findMany;
import static org.confeti.service.BaseEntityService.findOne;

@Service
public class ReportStatsService implements BaseEntityService {

    private final ReportStatsByConferenceDao statsByConferenceDao;
    private final ReportStatsBySpeakerForYearDao statsBySpeakerForYearDao;
    private final ReportStatsBySpeakerForConferenceDao statsBySpeakerForConferenceDao;
    private final ReportStatsByCompanyDao statsByCompanyDao;

    public ReportStatsService(final ReportStatsByConferenceDao statsByConferenceDao,
                              final ReportStatsBySpeakerForYearDao statsBySpeakerForYearDao,
                              final ReportStatsBySpeakerForConferenceDao statsBySpeakerForConferenceDao,
                              final ReportStatsByCompanyDao statsByCompanyDao) {
        this.statsByConferenceDao = statsByConferenceDao;
        this.statsBySpeakerForYearDao = statsBySpeakerForYearDao;
        this.statsBySpeakerForConferenceDao = statsBySpeakerForConferenceDao;
        this.statsByCompanyDao = statsByCompanyDao;
    }

    @NotNull
    public Mono<ReactiveRow> updateReportStatsByConference(@NotNull final String conferenceName,
                                                           @NotNull final Integer year) {
        return Mono.from(statsByConferenceDao.incrementReportTotal(conferenceName, year, 1L));
    }

    @NotNull
    public Mono<ReactiveRow> updateReportStatsBySpeakerForConference(@NotNull final String conferenceName,
                                                                     @NotNull final UUID speakerId) {
        return Mono.from(statsBySpeakerForConferenceDao.incrementReportTotal(speakerId, conferenceName, 1L));
    }

    @NotNull
    public Mono<ReactiveRow> updateReportStatsBySpeakerForYear(@NotNull final Integer year,
                                                               @NotNull final UUID speakerId) {
        return Mono.from(statsBySpeakerForYearDao.incrementReportTotal(speakerId, year, 1L));
    }

    @NotNull
    public Mono<ReactiveRow> updateReportStatsByCompany(@NotNull final Integer year,
                                                        @NotNull final String company) {
        return Mono.from(statsByCompanyDao.incrementReportTotal(company, year, 1L));
    }

    @NotNull
    static <T> Mono<T> updateReportStatsIf(final boolean needToIncrement,
                                           @NotNull final T entityThatReturn,
                                           @NotNull final Mono<?> incrementReportTotal) {
        if (needToIncrement) {
            return incrementReportTotal.then(Mono.just(entityThatReturn));
        }
        return Mono.just(entityThatReturn);
    }

    @NotNull
    public Flux<ReportStats> countConferenceStats(@NotNull final String conferenceName) {
        return findMany(
                statsByConferenceDao.findByConferenceName(conferenceName),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countConferenceStatsForYear(@NotNull final String conferenceName,
                                                         @NotNull final Integer year) {
        return findOne(
                statsByConferenceDao.findByConferenceNameByYear(conferenceName, year),
                ReportStats::from);
    }

    @NotNull
    public Flux<ReportStats> countSpeakerStatsForYears(@NotNull final UUID speakerId) {
        return findMany(
                statsBySpeakerForYearDao.findBySpeakerId(speakerId),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countSpeakerStatsForYear(@NotNull final UUID speakerId,
                                                      @NotNull final Integer year) {
        return findOne(
                statsBySpeakerForYearDao.findBySpeakerIdForYear(speakerId, year),
                ReportStats::from);
    }

    @NotNull
    public Flux<ReportStats> countSpeakerStatsForConferences(@NotNull final UUID speakerId) {
        return findMany(
                statsBySpeakerForConferenceDao.findBySpeakerId(speakerId),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countSpeakerStatsForConference(@NotNull final UUID speakerId,
                                                            @NotNull final String conferenceName) {
        return findOne(
                statsBySpeakerForConferenceDao.findBySpeakerIdForConference(speakerId, conferenceName),
                ReportStats::from);
    }

    @NotNull
    public Flux<ReportStats> countCompanyStats(@NotNull final String companyName) {
        return findMany(
                statsByCompanyDao.findByCompanyName(companyName),
                ReportStats::from);
    }

    @NotNull
    public Mono<ReportStats> countCompanyStatsForYear(@NotNull final String companyName,
                                                      @NotNull final Integer year) {
        return findOne(
                statsByCompanyDao.findByCompanyNameForYear(companyName, year),
                ReportStats::from);
    }
}
