package org.confeti.service.dto.stats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.report.stats.ReportStatsByConferenceEntity;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ReportStatsByConference extends ReportStats {

    private String conferenceName;

    private Integer year;

    @NotNull
    public static ReportStatsByConference from(@NotNull final ReportStatsByConference stats) {
        return ReportStatsByConference.builder()
                .conferenceName(stats.getConferenceName())
                .year(stats.getYear())
                .reportTotal(stats.getReportTotal())
                .build();
    }

    @NotNull
    public static ReportStatsByConference from(@NotNull final ReportStatsByConferenceEntity stats) {
        return ReportStatsByConference.builder()
                .conferenceName(stats.getConferenceName())
                .year(stats.getYear())
                .reportTotal(stats.getReportTotal())
                .build();
    }
}
