package org.confeti.db.mapper.report;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForYearDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.report.stats.AbstractReportStatsEntity.REPORT_STATS_ATT_REPORT_TOTAL;
import static org.confeti.db.model.report.stats.ReportStatsByCompanyEntity.STATS_BY_COMPANY_ATT_COMPANY_NAME;
import static org.confeti.db.model.report.stats.ReportStatsByCompanyEntity.STATS_BY_COMPANY_ATT_YEAR;
import static org.confeti.db.model.report.stats.ReportStatsByCompanyEntity.STATS_BY_COMPANY_TABLE;
import static org.confeti.db.model.report.stats.ReportStatsByConferenceEntity.STATS_BY_CONF_ATT_CONF_NAME;
import static org.confeti.db.model.report.stats.ReportStatsByConferenceEntity.STATS_BY_CONF_ATT_YEAR;
import static org.confeti.db.model.report.stats.ReportStatsByConferenceEntity.STATS_BY_CONF_TABLE;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity.STATS_BY_SPEAKER_FOR_CONF_ATT_CONF_NAME;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity.STATS_BY_SPEAKER_FOR_CONF_ATT_SPEAKER_ID;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity.STATS_BY_SPEAKER_FOR_CONF_TABLE;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForYearEntity.STATS_BY_SPEAKER_FOR_YEAR_ATT_SPEAKER_ID;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForYearEntity.STATS_BY_SPEAKER_FOR_YEAR_ATT_YEAR;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForYearEntity.STATS_BY_SPEAKER_FOR_YEAR_TABLE;

@Mapper
public interface ReportStatsDaoMapper extends BaseMapper {

    @DaoFactory
    ReportStatsByConferenceDao reportStatsByConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReportStatsBySpeakerForConferenceDao reportStatsBySpeakerForConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReportStatsBySpeakerForYearDao reportStatsBySpeakerForYearDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReportStatsByCompanyDao reportStatsByCompanyDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report_stats_by_conference</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_stats_by_conference (
     *     conference_name text,
     *     year int,
     *     report_total counter,
     *     PRIMARY KEY (conference_name, year)
     * ) WITH CLUSTERING ORDER BY (year DESC);
     * </pre>
     */
    default void createReportStatsByConferenceTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(STATS_BY_CONF_TABLE).ifNotExists()
                .withPartitionKey(STATS_BY_CONF_ATT_CONF_NAME, DataTypes.TEXT)
                .withClusteringColumn(STATS_BY_CONF_ATT_YEAR, DataTypes.INT)
                .withColumn(REPORT_STATS_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .withClusteringOrder(STATS_BY_CONF_ATT_YEAR, ClusteringOrder.DESC)
                .build());
    }

    /**
     * Create the <i>report_stats_by_speaker_for_conference</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_stats_by_speaker_for_conference (
     *     speaker_id uuid,
     *     conference_name text,
     *     report_total counter,
     *     PRIMARY KEY (speaker_id, conference_name)
     * );
     * </pre>
     */
    default void createReportStatsBySpeakerForConfTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(STATS_BY_SPEAKER_FOR_CONF_TABLE).ifNotExists()
                .withPartitionKey(STATS_BY_SPEAKER_FOR_CONF_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(STATS_BY_SPEAKER_FOR_CONF_ATT_CONF_NAME, DataTypes.TEXT)
                .withColumn(REPORT_STATS_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .build());
    }

    /**
     * Create the <i>report_stats_by_speaker_for_year</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_stats_by_speaker_for_year (
     *     speaker_id uuid,
     *     year int,
     *     report_total counter,
     *     PRIMARY KEY (speaker_id, year, conference_name)
     * ) WITH CLUSTERING ORDER BY (year DESC, conference_name ASC);
     * </pre>
     */
    default void createReportStatsBySpeakerForYearTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(STATS_BY_SPEAKER_FOR_YEAR_TABLE).ifNotExists()
                .withPartitionKey(STATS_BY_SPEAKER_FOR_YEAR_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(STATS_BY_SPEAKER_FOR_YEAR_ATT_YEAR, DataTypes.INT)
                .withColumn(REPORT_STATS_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .withClusteringOrder(STATS_BY_SPEAKER_FOR_YEAR_ATT_YEAR, ClusteringOrder.DESC)
                .build());
    }

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
    default void createReportStatsByCompanyTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(STATS_BY_COMPANY_TABLE).ifNotExists()
                .withPartitionKey(STATS_BY_COMPANY_ATT_COMPANY_NAME, DataTypes.TEXT)
                .withClusteringColumn(STATS_BY_COMPANY_ATT_YEAR, DataTypes.INT)
                .withColumn(REPORT_STATS_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .withClusteringOrder(STATS_BY_COMPANY_ATT_YEAR, ClusteringOrder.DESC)
                .build());
    }
}
