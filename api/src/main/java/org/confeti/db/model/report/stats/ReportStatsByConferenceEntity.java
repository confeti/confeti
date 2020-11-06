package org.confeti.db.model.report.stats;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportStatsByConferenceEntity.REPORT_STATS_BY_CONFERENCE_TABLE)
public class ReportStatsByConferenceEntity extends AbstractReportStatsEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_STATS_BY_CONFERENCE_TABLE = "report_stats_by_conference";
    public static final String REPORT_STATS_BY_CONFERENCE_ATT_CONFERENCE_NAME = "conference_name";
    public static final String REPORT_STATS_BY_CONFERENCE_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(REPORT_STATS_BY_CONFERENCE_ATT_CONFERENCE_NAME)
    private String conferenceName;

    @ClusteringColumn
    @CqlName(REPORT_STATS_BY_CONFERENCE_ATT_YEAR)
    private Integer year;
}
