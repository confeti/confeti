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
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_NAME;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_ATT_LOCATIONS;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_ATT_YEAR;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_TABLE;
import static org.confeti.db.model.udt.SpeakerLocationUDT.SPEAKER_LOCATION_UDT;

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
     *     avatar text,
     *     locations set&lt;frozen&lt;speaker_location&gt;&gt;,
     *     id uuid,
     *     PRIMARY KEY (conference_name, year, name)
     * ) WITH CLUSTERING ORDER BY (year DESC, name ASC);
     * </pre>
     */
    default void createSchema(@NotNull final CqlSession cqlSession) {
        createSpeakerLocationUDT(cqlSession);
        cqlSession.execute(createTable(SPEAKER_BY_CONFERENCE_TABLE).ifNotExists()
                .withPartitionKey(SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME, DataTypes.TEXT)
                .withClusteringColumn(SPEAKER_BY_CONFERENCE_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_AVATAR, DataTypes.TEXT)
                .withColumn(SPEAKER_BY_CONFERENCE_ATT_LOCATIONS, DataTypes.setOf(udt(SPEAKER_LOCATION_UDT, true)))
                .withColumn(SPEAKER_ATT_ID, DataTypes.UUID)
                .withClusteringOrder(SPEAKER_BY_CONFERENCE_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(SPEAKER_ATT_NAME, ClusteringOrder.ASC)
                .build());
    }
}
