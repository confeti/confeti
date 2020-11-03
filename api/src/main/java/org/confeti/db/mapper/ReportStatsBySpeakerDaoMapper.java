package org.confeti.db.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.ReportStatsBySpeakerDao;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME;
import static org.confeti.db.model.ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_ATT_REPORT_TOTAL;
import static org.confeti.db.model.ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_ATT_SPEAKER_ID;
import static org.confeti.db.model.ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_ATT_YEAR;
import static org.confeti.db.model.ReportStatsBySpeakerEntity.REPORT_STATS_BY_SPEAKER_TABLE;

@Mapper
public interface ReportStatsBySpeakerDaoMapper extends BaseMapper {

    @DaoFactory
    ReportStatsBySpeakerDao reportStatsBySpeakerDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>report_stats_by_speaker</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS report_stats_by_speaker (
     *     speaker_id uuid,
     *     conference_name text,
     *     year int,
     *     report_total counter,
     *     PRIMARY KEY (speaker_id, year, conference_name)
     * ) WITH CLUSTERING ORDER BY (year DESC, conference_name ASC);
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(REPORT_STATS_BY_SPEAKER_TABLE).ifNotExists()
                .withPartitionKey(REPORT_STATS_BY_SPEAKER_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(REPORT_STATS_BY_SPEAKER_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME, DataTypes.TEXT)
                .withColumn(REPORT_STATS_BY_SPEAKER_ATT_REPORT_TOTAL, DataTypes.COUNTER)
                .withClusteringOrder(REPORT_STATS_BY_SPEAKER_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(REPORT_STATS_BY_SPEAKER_ATT_CONFERENCE_NAME, ClusteringOrder.ASC)
                .build());
    }
}
