package org.confeti.db.mapper.report;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_COMPLEXITY;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_CONFERENCES;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_DESCRIPTION;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_ID;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_LANGUAGE;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SOURCE;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SPEAKERS;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TITLE;
import static org.confeti.db.model.report.ReportEntity.REPORT_TABLE;
import static org.confeti.db.model.udt.ConferenceShortInfoUDT.CONFERENCE_SHORT_INFO_UDT;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;
import static org.confeti.db.model.udt.SpeakerFullInfoUDT.SPEAKER_FULL_INFO_UDT;

@Mapper
public interface ReportDaoMapper extends BaseMapper {

    @DaoFactory
    ReportDao reportDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report</i> table.
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
    default void createSchema(@NotNull final CqlSession cqlSession) {
        createConferenceShortInfoUDT(cqlSession);
        createReportSourceUDT(cqlSession);
        createSpeakerFullInfoUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_TABLE).ifNotExists()
                .withPartitionKey(REPORT_ATT_ID, DataTypes.UUID)
                .withColumn(REPORT_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_ATT_CONFERENCES, DataTypes.setOf(udt(CONFERENCE_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_ATT_DESCRIPTION, DataTypes.TEXT)
                .withColumn(REPORT_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_ATT_SPEAKERS, DataTypes.setOf(udt(SPEAKER_FULL_INFO_UDT, true)))
                .withColumn(REPORT_ATT_TAGS, DataTypes.setOf(DataTypes.TEXT))
                .withColumn(REPORT_ATT_TITLE, DataTypes.TEXT)
                .build());
    }
}
