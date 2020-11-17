package org.confeti.db.model.report.stats;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
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
@CqlName(ReportStatsBySpeakerForYearEntity.STATS_BY_SPEAKER_FOR_YEAR_TABLE)
public class ReportStatsBySpeakerForYearEntity extends AbstractReportStatsEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATS_BY_SPEAKER_FOR_YEAR_TABLE = "report_stats_by_speaker_for_year";
    public static final String STATS_BY_SPEAKER_FOR_YEAR_ATT_SPEAKER_ID = "speaker_id";
    public static final String STATS_BY_SPEAKER_FOR_YEAR_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(STATS_BY_SPEAKER_FOR_YEAR_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn
    @CqlName(STATS_BY_SPEAKER_FOR_YEAR_ATT_YEAR)
    private Integer year;

    @Override
    public Long getReportTotal() {
        return reportTotal;
    }
}
