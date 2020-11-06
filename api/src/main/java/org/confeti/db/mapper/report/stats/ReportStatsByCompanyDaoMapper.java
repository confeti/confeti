package org.confeti.db.mapper.report.stats;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.report.stats.AbstractReportStatsEntity.REPORT_STATS_ATT_REPORT_TOTAL;
import static org.confeti.db.model.report.stats.ReportStatsByCompanyEntity.REPORT_STATS_BY_COMPANY_ATT_COMPANY_NAME;
import static org.confeti.db.model.report.stats.ReportStatsByCompanyEntity.REPORT_STATS_BY_COMPANY_ATT_YEAR;
import static org.confeti.db.model.report.stats.ReportStatsByCompanyEntity.REPORT_STATS_BY_COMPANY_TABLE;

@Mapper
public interface ReportStatsByCompanyDaoMapper extends BaseMapper {

    @DaoFactory
    ReportStatsByCompanyDao reportStatsByCompanyDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report_stats_by_company</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_stats_by_company (
     *     company_name text,
     *     year int,
     *     report_total counter,
     *     PRIMARY KEY (company_name, year)
     * ) WITH CLUSTERING ORDER BY (year DESC);
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(REPORT_STATS_BY_COMPANY_TABLE).ifNotExists()
                .withPartitionKey(REPORT_STATS_BY_COMPANY_ATT_COMPANY_NAME, DataTypes.TEXT)
                .withClusteringColumn(REPORT_STATS_BY_COMPANY_ATT_YEAR, DataTypes.INT)
                .withColumn(REPORT_STATS_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .withClusteringOrder(REPORT_STATS_BY_COMPANY_ATT_YEAR, ClusteringOrder.DESC)
                .build());
    }
}
