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
@CqlName(ReportStatsByCompanyEntity.STATS_BY_COMPANY_TABLE)
public class ReportStatsByCompanyEntity extends AbstractReportStatsEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATS_BY_COMPANY_TABLE = "report_stats_by_company";
    public static final String STATS_BY_COMPANY_ATT_COMPANY_NAME = "company_name";
    public static final String STATS_BY_COMPANY_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(STATS_BY_COMPANY_ATT_COMPANY_NAME)
    private String companyName;

    @ClusteringColumn
    @CqlName(STATS_BY_COMPANY_ATT_YEAR)
    private Integer year;

    @Override
    public Long getReportTotal() {
        return reportTotal;
    }
}
