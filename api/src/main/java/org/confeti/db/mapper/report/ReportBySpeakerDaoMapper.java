package org.confeti.db.mapper.report;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_COMPLEXITY;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_ID;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_LANGUAGE;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_SOURCE;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_TITLE;
import static org.confeti.db.model.report.ReportBySpeakerEntity.REPORT_BY_SPEAKER_ATT_SPEAKER_ID;
import static org.confeti.db.model.report.ReportBySpeakerEntity.REPORT_BY_SPEAKER_ATT_YEAR;
import static org.confeti.db.model.report.ReportBySpeakerEntity.REPORT_BY_SPEAKER_TABLE;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_CONFERENCES;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_DESCRIPTION;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;
import static org.confeti.db.model.udt.ConferenceShortInfoUDT.CONFERENCE_SHORT_INFO_UDT;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;

@Mapper
public interface ReportBySpeakerDaoMapper extends BaseMapper {

    @DaoFactory
    ReportBySpeakerDao reportBySpeakerDao(@DaoKeyspace CqlIdentifier keyspace);

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
     * ) WITH CLUSTERING ORDER BY (year DESC, title ASC);
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        createConferenceShortInfoUDT(cqlSession);
        createReportSourceUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_BY_SPEAKER_TABLE).ifNotExists()
                .withPartitionKey(REPORT_BY_SPEAKER_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(REPORT_BY_SPEAKER_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .withClusteringColumn(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_CONFERENCES, DataTypes.setOf(udt(CONFERENCE_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_DESCRIPTION, DataTypes.TEXT)
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_TAGS, DataTypes.setOf(DataTypes.TEXT))
                .withClusteringOrder(REPORT_BY_SPEAKER_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(REPORT_ATT_TITLE, ClusteringOrder.ASC)
                .build());
    }
}
