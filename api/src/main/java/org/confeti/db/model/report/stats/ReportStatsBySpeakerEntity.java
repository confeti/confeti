package org.confeti.db.model.report.stats;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_TABLE)
public class ReportStatsBySpeakerEntity extends AbstractReportStatsEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_STATS_BY_SPEAKER_TABLE = "report_stats_by_speaker";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME = "conference_name";
    public static final String REPORT_STATS_BY_SPEAKER_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(1)
    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_YEAR)
    private Integer year;

    @ClusteringColumn(2)
    @CqlName(REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME)
    private String conferenceName;
}
