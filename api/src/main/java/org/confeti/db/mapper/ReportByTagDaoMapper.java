package org.confeti.db.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.ReportByTagDao;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_COMPLEXITY;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_CONFERENCES;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_LANGUAGE;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_REPORT_ID;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_SOURCE;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_SPEAKERS;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_TAG_NAME;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_ATT_TITLE;
import static org.confeti.db.model.ReportByTagEntity.REPORT_BY_TAG_TABLE;
import static org.confeti.db.model.udt.ConferenceShortInfoUDT.CONFERENCE_SHORT_INFO_UDT;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;
import static org.confeti.db.model.udt.SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT;

@Mapper
public interface ReportByTagDaoMapper extends BaseMapper {

    @DaoFactory
    ReportByTagDao reportByTagDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report_by_tag</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_by_tag (
     *     tag_name text,
     *     title text,
     *     complexity int,
     *     conferences set&lt;frozen&lt;conference_short_info&gt;&gt;,
     *     language text,
     *     report_id uuid,
     *     source frozen&lt;report_source&gt;,
     *     speakers set&lt;frozen&lt;speaker_short_info&gt;&gt,
     *     PRIMARY KEY (tag_name, title)
     * );
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        createConferenceShortInfoUDT(cqlSession);
        createReportSourceUDT(cqlSession);
        createSpeakerShortInfoUDT(cqlSession);
        cqlSession.execute(createTable(REPORT_BY_TAG_TABLE).ifNotExists()
                .withPartitionKey(REPORT_BY_TAG_ATT_TAG_NAME, DataTypes.TEXT)
                .withClusteringColumn(REPORT_BY_TAG_ATT_TITLE, DataTypes.TEXT)
                .withColumn(REPORT_BY_TAG_ATT_COMPLEXITY, DataTypes.INT)
                .withColumn(REPORT_BY_TAG_ATT_CONFERENCES, DataTypes.setOf(udt(CONFERENCE_SHORT_INFO_UDT, true)))
                .withColumn(REPORT_BY_TAG_ATT_LANGUAGE, DataTypes.TEXT)
                .withColumn(REPORT_BY_TAG_ATT_REPORT_ID, DataTypes.UUID)
                .withColumn(REPORT_BY_TAG_ATT_SOURCE, udt(REPORT_SOURCE_UDT, true))
                .withColumn(REPORT_BY_TAG_ATT_SPEAKERS, DataTypes.setOf(udt(SPEAKER_SHORT_INFO_UDT, true)))
                .build());
    }
}
