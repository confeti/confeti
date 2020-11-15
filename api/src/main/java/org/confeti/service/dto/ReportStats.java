package org.confeti.service.dto;

import lombok.Data;
import org.confeti.db.model.report.stats.ReportStatsByCompanyEntity;
import org.confeti.db.model.report.stats.ReportStatsByConferenceEntity;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerForYearEntity;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
public class ReportStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private final Long reportTotal;

    @NotNull
    public static ReportStats from(@NotNull final ReportStats stats) {
        return new ReportStats(stats.getReportTotal());
    }

    @NotNull
    public static ReportStats from(@NotNull final ReportStatsByConferenceEntity stats) {
        return new ReportStats(stats.getReportTotal());
    }

    @NotNull
    public static ReportStats from(@NotNull final ReportStatsBySpeakerForYearEntity stats) {
        return new ReportStats(stats.getReportTotal());
    }

    @NotNull
    public static ReportStats from(@NotNull final ReportStatsBySpeakerForConferenceEntity stats) {
        return new ReportStats(stats.getReportTotal());
    }

    @NotNull
    public static ReportStats from(@NotNull final ReportStatsByCompanyEntity stats) {
        return new ReportStats(stats.getReportTotal());
    }
}
