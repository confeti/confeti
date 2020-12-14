package org.confeti.db.mapper.speaker;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.dao.speaker.SpeakerDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createIndex;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONF_ATT_CONF_NAME;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONF_ATT_LOCATION;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONF_ATT_YEAR;
import static org.confeti.db.model.speaker.SpeakerByConferenceEntity.SPEAKER_BY_CONF_TABLE;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_BIO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_CONTACT_INFO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_NAME;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_NAME_INDEX;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_TABLE;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_UDT;

@Mapper
public interface SpeakerDaoMapper extends BaseMapper {

    @DaoFactory
    SpeakerDao speakerDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    SpeakerByConferenceDao speakerByConferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>speaker</i> table. And index on a <i>name</i> column.
     *
     * <pre>
     * CREATE INDEX IF NOT EXISTS speaker_name_idx ON speaker(name);
     *
     * CREATE TABLE IF NOT EXISTS speaker (
     *     id uuid,
     *     name text,
     *     avatar text,
     *     bio text,
     *     contact_info frozen&lt;contact_info&gt;,
     *     PRIMARY KEY (id, name)
     * );
     * </pre>
     */
    default void createSpeakerTable(@NotNull final CqlSession cqlSession) {
        createContactInfoUDT(cqlSession);
        cqlSession.execute(createTable(SPEAKER_TABLE).ifNotExists()
                .withPartitionKey(SPEAKER_ATT_ID, DataTypes.UUID)
                .withClusteringColumn(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_AVATAR, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_BIO, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_CONTACT_INFO, udt(CONTACT_INFO_UDT, true))
                .build());

        cqlSession.execute(createIndex(SPEAKER_NAME_INDEX).ifNotExists()
                .onTable(SPEAKER_TABLE)
                .andColumn(SPEAKER_ATT_NAME)
                .build());
    }

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
     * ) WITH CLUSTERING ORDER BY (year DESC, name ASC, id ASC);
     * </pre>
     */
    default void createSpeakerByConferenceTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(SPEAKER_BY_CONF_TABLE).ifNotExists()
                .withPartitionKey(SPEAKER_BY_CONF_ATT_CONF_NAME, DataTypes.TEXT)
                .withClusteringColumn(SPEAKER_BY_CONF_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .withClusteringColumn(SPEAKER_ATT_ID, DataTypes.UUID)
                .withColumn(SPEAKER_ATT_AVATAR, DataTypes.TEXT)
                .withColumn(SPEAKER_BY_CONF_ATT_LOCATION, DataTypes.TEXT)
                .withClusteringOrder(SPEAKER_BY_CONF_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(SPEAKER_ATT_NAME, ClusteringOrder.ASC)
                .withClusteringOrder(SPEAKER_ATT_ID, ClusteringOrder.ASC)
                .build());
    }
}
