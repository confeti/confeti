package org.confeti.db.model.report.stats;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportStatsByConferenceEntity.STATS_BY_CONF_TABLE)
public class ReportStatsByConferenceEntity extends AbstractReportStatsEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATS_BY_CONF_TABLE = "report_stats_by_conference";
    public static final String STATS_BY_CONF_ATT_CONF_NAME = "conference_name";
    public static final String STATS_BY_CONF_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(STATS_BY_CONF_ATT_CONF_NAME)
    private String conferenceName;

    @ClusteringColumn
    @CqlName(STATS_BY_CONF_ATT_YEAR)
    private Integer year;

    @Override
    public Long getReportTotal() {
        return reportTotal;
    }
}
