package org.confeti.db.mapper.report.stats;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForConferenceDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.report.stats.AbstractReportStatsEntity.REPORT_STATS_ATT_REPORT_TOTAL;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity.REPORT_STATS_BY_SPEAKER_FOR_CONFERENCE_ATT_CONFERENCE_NAME;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity.REPORT_STATS_BY_SPEAKER_FOR_CONFERENCE_ATT_SPEAKER_ID;
import static org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity.REPORT_STATS_BY_SPEAKER_FOR_CONFERENCE_TABLE;

@Mapper
public interface ReportStatsBySpeakerForConferenceDaoMapper extends BaseMapper {

    @DaoFactory
    ReportStatsBySpeakerForConferenceDao reportStatsBySpeakerForConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

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
    default void createSchema(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(REPORT_STATS_BY_SPEAKER_FOR_CONFERENCE_TABLE).ifNotExists()
                .withPartitionKey(REPORT_STATS_BY_SPEAKER_FOR_CONFERENCE_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(REPORT_STATS_BY_SPEAKER_FOR_CONFERENCE_ATT_CONFERENCE_NAME, DataTypes.TEXT)
                .withColumn(REPORT_STATS_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .build());
    }
}
