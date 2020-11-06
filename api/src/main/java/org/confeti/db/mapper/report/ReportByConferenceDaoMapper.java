package org.confeti.db.mapper.report;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_COMPLEXITY;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_ID;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_LANGUAGE;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_SOURCE;
import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_TITLE;
import static org.confeti.db.model.report.ReportByConferenceEntity.REPORT_BY_CONFERENCE_ATT_CONFERENCE_NAME;
import static org.confeti.db.model.report.ReportByConferenceEntity.REPORT_BY_CONFERENCE_ATT_YEAR;
import static org.confeti.db.model.report.ReportByConferenceEntity.REPORT_BY_CONFERENCE_TABLE;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SPEAKERS;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;
import static org.confeti.db.model.udt.SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT;

@Mapper
public interface ReportByConferenceDaoMapper extends BaseMapper {

    @DaoFactory
    ReportByConferenceDao reportByConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report_by_conference</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_by_conference (
     *     conference_name text,
     *     year int,
     *     title text,
     *     complexity int,
     *     language text,
     *     id uuid,
     *     source frozen&lt;report_source&gt;,
     *     speakers set&lt;frozen&lt;speaker_short_info&gt;&gt;,
     *     tags set&lt;text&gt;,
     *     PRIMARY KEY (conference_name, year, title)
     * ) WITH CLUSTERING ORDER BY (year DESC, title ASC);
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        createReportSourceUDT(cqlSession);
        createSpeakerShortInfoUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_BY_CONFERENCE_TABLE).ifNotExists()
                .withPartitionKey(REPORT_BY_CONFERENCE_ATT_CONFERENCE_NAME, DataTypes.TEXT)
                .withClusteringColumn(REPORT_BY_CONFERENCE_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_SPEAKERS, DataTypes.setOf(udt(SPEAKER_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_TAGS, DataTypes.setOf(DataTypes.TEXT))
                .withClusteringOrder(REPORT_BY_CONFERENCE_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(REPORT_ATT_TITLE, ClusteringOrder.ASC)
                .build());
    }
}
