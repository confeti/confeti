package org.confeti.db.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_TABLE)
public class ReportStatsBySpeakerEntity {

    private static final long serialVersionUID = 0L;

    public static final String REPORT_STATS_BY_SPEAKER_TABLE = "report_stats_by_speaker";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME = "conference_name";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_YEAR = "year";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_REPORT_TOTAL = "report_total";

    @PartitionKey
    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(1)
    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_YEAR)
    private Integer year;

    @ClusteringColumn(2)
    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME)
    private String conferenceName;

    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_REPORT_TOTAL)
    private Long reportTotal;
}
