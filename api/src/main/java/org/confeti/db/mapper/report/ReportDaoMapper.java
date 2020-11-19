package org.confeti.db.mapper.report;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createIndex;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_COMPLEXITY;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_ID;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_LANGUAGE;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_SOURCE;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_TITLE;
import static org.confeti.db.model.report.ReportByConferenceEntity.REPORT_BY_CONF_ATT_CONF_NAME;
import static org.confeti.db.model.report.ReportByConferenceEntity.REPORT_BY_CONF_ATT_YEAR;
import static org.confeti.db.model.report.ReportByConferenceEntity.REPORT_BY_CONF_TABLE;
import static org.confeti.db.model.report.ReportBySpeakerEntity.REPORT_BY_SPEAKER_ATT_SPEAKER_ID;
import static org.confeti.db.model.report.ReportBySpeakerEntity.REPORT_BY_SPEAKER_ATT_YEAR;
import static org.confeti.db.model.report.ReportBySpeakerEntity.REPORT_BY_SPEAKER_TABLE;
import static org.confeti.db.model.report.ReportByTagEntity.REPORT_BY_TAG_ATT_TAG_NAME;
import static org.confeti.db.model.report.ReportByTagEntity.REPORT_BY_TAG_TABLE;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_CONFERENCES;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_DESCRIPTION;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SPEAKERS;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;
import static org.confeti.db.model.report.ReportEntity.REPORT_TABLE;
import static org.confeti.db.model.report.ReportEntity.REPORT_TITLE_INDEX;
import static org.confeti.db.model.udt.ConferenceShortInfoUDT.CONF_SHORT_INFO_UDT;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;
import static org.confeti.db.model.udt.SpeakerFullInfoUDT.SPEAKER_FULL_INFO_UDT;
import static org.confeti.db.model.udt.SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT;

@Mapper
public interface ReportDaoMapper extends BaseMapper {

    @DaoFactory
    ReportDao reportDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReportByConferenceDao reportByConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReportBySpeakerDao reportBySpeakerDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReportByTagDao reportByTagDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report</i> table. And index on a <i>title</i> column.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report (
     *     id uuid,
     *     complexity int,
     *     conferences set&lt;frozen&lt;conference_short_info&gt;&gt;,
     *     description text,
     *     language text,
     *     source frozen&lt;report_source&gt;,
     *     speakers set&lt;frozen&lt;speaker_full_info&gt;&gt;,
     *     tags set&lt;text&gt;,
     *     title text,
     *     PRIMARY KEY (id)
     * );
     * </pre>
     */
    default void createReportTable(@NotNull final CqlSession cqlSession) {
        createConferenceShortInfoUDT(cqlSession);
        createReportSourceUDT(cqlSession);
        createSpeakerFullInfoUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_TABLE).ifNotExists()
                .withPartitionKey(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_CONFERENCES, DataTypes.setOf(udt(CONF_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_DESCRIPTION, DataTypes.TEXT)
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_SPEAKERS, DataTypes.setOf(udt(SPEAKER_FULL_INFO_UDT, true)))
                .withColumn(REPORT_ATT_TAGS, DataTypes.setOf(DataTypes.TEXT))
                .withColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .build());

        cqlSession.execute(createIndex(REPORT_TITLE_INDEX).ifNotExists()
                .onTable(REPORT_TABLE)
                .andColumn(REPORT_ATT_TITLE)
                .build());
    }

    /**
     * Create the <i>report_by_conference</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_by_conference (
     *     conference_name text,
     *     year int,
     *     title text,
     *     id uuid,
     *     complexity int,
     *     language text,
     *     source frozen&lt;report_source&gt;,
     *     speakers set&lt;frozen&lt;speaker_short_info&gt;&gt;,
     *     tags set&lt;text&gt;,
     *     PRIMARY KEY (conference_name, year, title, id)
     * ) WITH CLUSTERING ORDER BY (year DESC, title ASC, id ASC);
     * </pre>
     */
    default void createReportByConferenceTable(@NotNull final CqlSession cqlSession) {
        createReportSourceUDT(cqlSession);
        createSpeakerShortInfoUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_BY_CONF_TABLE).ifNotExists()
                .withPartitionKey(REPORT_BY_CONF_ATT_CONF_NAME, DataTypes.TEXT)
                .withClusteringColumn(REPORT_BY_CONF_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .withClusteringColumn(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_SPEAKERS, DataTypes.setOf(udt(SPEAKER_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_TAGS, DataTypes.setOf(DataTypes.TEXT))
                .withClusteringOrder(REPORT_BY_CONF_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(REPORT_ATT_TITLE, ClusteringOrder.ASC)
                .withClusteringOrder(REPORT_ATT_ID, ClusteringOrder.ASC)
                .build());
    }

    /**
     * Create the <i>report_by_speaker</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_by_speaker (
     *     speaker_id uuid,
     *     year int,
     *     title text,
     *     id uuid,
     *     complexity int,
     *     conferences set&lt;frozen&lt;conference_short_info&gt;&gt;,
     *     description text,
     *     language text,
     *     source frozen&lt;report_source&gt;,
     *     tags set&lt;text&gt;,
     *     PRIMARY KEY (speaker_id, year, title, id)
     * ) WITH CLUSTERING ORDER BY (year DESC, title ASC, id ASC);
     * </pre>
     */
    default void createReportBySpeakerTable(@NotNull final CqlSession cqlSession) {
        createConferenceShortInfoUDT(cqlSession);
        createReportSourceUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_BY_SPEAKER_TABLE).ifNotExists()
                .withPartitionKey(REPORT_BY_SPEAKER_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(REPORT_BY_SPEAKER_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .withClusteringColumn(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_CONFERENCES, DataTypes.setOf(udt(CONF_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_DESCRIPTION, DataTypes.TEXT)
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_TAGS, DataTypes.setOf(DataTypes.TEXT))
                .withClusteringOrder(REPORT_BY_SPEAKER_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(REPORT_ATT_TITLE, ClusteringOrder.ASC)
                .withClusteringOrder(REPORT_ATT_ID, ClusteringOrder.ASC)
                .build());
    }

    /**
     * Create the <i>report_by_tag</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_by_tag (
     *     tag_name text,
     *     title text,
     *     id uuid,
     *     complexity int,
     *     conferences set&lt;frozen&lt;conference_short_info&gt;&gt;,
     *     language text,
     *     source frozen&lt;report_source&gt;,
     *     speakers set&lt;frozen&lt;speaker_short_info&gt;&gt,
     *     PRIMARY KEY (tag_name, title, id)
     * );
     * </pre>
     */
    default void createReportByTagTable(@NotNull final CqlSession cqlSession) {
        createConferenceShortInfoUDT(cqlSession);
        createReportSourceUDT(cqlSession);
        createSpeakerShortInfoUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_BY_TAG_TABLE).ifNotExists()
                .withPartitionKey(REPORT_BY_TAG_ATT_TAG_NAME, DataTypes.TEXT)
                .withClusteringColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .withClusteringColumn(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_CONFERENCES, DataTypes.setOf(udt(CONF_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_SPEAKERS, DataTypes.setOf(udt(SPEAKER_SHORT_INFO_UDT, true)))
                .build());
    }
}
