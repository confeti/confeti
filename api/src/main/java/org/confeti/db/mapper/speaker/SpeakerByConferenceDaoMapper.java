package org.confeti.db.mapper.speaker;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_NAME;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_ATT_LOCATION;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_ATT_YEAR;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_TABLE;

@Mapper
public interface SpeakerByConferenceDaoMapper extends BaseMapper {

    @DaoFactory
    SpeakerByConferenceDao speakerByConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>speaker_by_conference</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS speaker_by_conference (
     *     conference_name text,
     *     year int,
     *     name text,
     *     id uuid,
     *     avatar text,
     *     location text,
     *     PRIMARY KEY (conference_name, year, name, id)
     * ) WITH CLUSTERING ORDER BY (year DESC, name ASC);
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(SPEAKER_BY_CONFERENCE_TABLE).ifNotExists()
                .withPartitionKey(SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME, DataTypes.TEXT)
                .withClusteringColumn(SPEAKER_BY_CONFERENCE_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .withClusteringColumn(SPEAKER_ATT_ID, DataTypes.UUID)
                .withColumn(SPEAKER_ATT_AVATAR, DataTypes.TEXT)
                .withColumn(SPEAKER_BY_CONFERENCE_ATT_LOCATION, DataTypes.TEXT)
                .withClusteringOrder(SPEAKER_BY_CONFERENCE_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(SPEAKER_ATT_NAME, ClusteringOrder.ASC)
                .build());
    }
}
