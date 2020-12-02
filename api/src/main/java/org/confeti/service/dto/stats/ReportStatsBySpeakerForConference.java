package org.confeti.service.dto.stats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ReportStatsBySpeakerForConference extends ReportStats {

    private UUID speakerId;

    private String conferenceName;

    @NotNull
    public static ReportStatsBySpeakerForConference from(@NotNull final ReportStatsBySpeakerForConference stats) {
        return ReportStatsBySpeakerForConference.builder()
                .speakerId(stats.getSpeakerId())
                .conferenceName(stats.getConferenceName())
                .reportTotal(stats.getReportTotal())
                .build();
    }

    @NotNull
    public static ReportStatsBySpeakerForConference from(@NotNull final ReportStatsBySpeakerForConferenceEntity stats) {
        return ReportStatsBySpeakerForConference.builder()
                .speakerId(stats.getSpeakerId())
                .conferenceName(stats.getConferenceName())
                .reportTotal(stats.getReportTotal())
                .build();
    }
}
