package org.confeti.service.dto.stats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerForYearEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ReportStatsBySpeakerForYear extends ReportStats {

    private UUID speakerId;

    private Integer year;

    @NotNull
    public static ReportStatsBySpeakerForYear from(@NotNull final ReportStatsBySpeakerForYear stats) {
        return ReportStatsBySpeakerForYear.builder()
                .speakerId(stats.getSpeakerId())
                .year(stats.getYear())
                .reportTotal(stats.getReportTotal())
                .build();
    }

    @NotNull
    public static ReportStatsBySpeakerForYear from(@NotNull final ReportStatsBySpeakerForYearEntity stats) {
        return ReportStatsBySpeakerForYear.builder()
                .speakerId(stats.getSpeakerId())
                .year(stats.getYear())
                .reportTotal(stats.getReportTotal())
                .build();
    }
}
