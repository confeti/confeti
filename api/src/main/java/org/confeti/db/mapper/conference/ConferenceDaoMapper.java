package org.confeti.db.mapper.conference;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.conference.ConferenceBySpeakerDao;
import org.confeti.db.dao.conference.ConferenceDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.conference.ConferenceBySpeakerEntity.CONF_BY_SPEAKER_ATT_SPEAKER_ID;
import static org.confeti.db.model.conference.ConferenceBySpeakerEntity.CONF_BY_SPEAKER_TABLE;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_LOCATION;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_LOGO;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_NAME;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_URL;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_YEAR;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_TABLE;

@Mapper
public interface ConferenceDaoMapper extends BaseMapper {

    @DaoFactory
    ConferenceDao conferenceDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ConferenceBySpeakerDao conferenceBySpeakerDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>conference</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS conference (
     *     name text,
     *     year int,
     *     location text,
     *     logo text STATIC,
     *     url text,
     *     PRIMARY KEY (name, year)
     * ) WITH CLUSTERING ORDER BY (year DESC);
     * </pre>
     */
    default void createConferenceTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(CONF_TABLE).ifNotExists()
                .withPartitionKey(CONF_ATT_NAME, DataTypes.TEXT)
                .withClusteringColumn(CONF_ATT_YEAR, DataTypes.INT)
                .withColumn(CONF_ATT_LOCATION, DataTypes.TEXT)
                .withStaticColumn(CONF_ATT_LOGO, DataTypes.TEXT)
                .withColumn(CONF_ATT_URL, DataTypes.TEXT)
                .withClusteringOrder(CONF_ATT_YEAR, ClusteringOrder.DESC)
                .build());
    }

    /**
     * Create the <i>conference_by_speaker</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS conference_by_speaker (
     *     speaker_id uuid,
     *     year int,
     *     name text,
     *     location text,
     *     logo text,
     *     url text,
     *     PRIMARY KEY (speaker_id, year, name)
     * ) WITH CLUSTERING ORDER BY (year DESC, name ASC);
     * </pre>
     */
    default void createConferenceBySpeakerTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(CONF_BY_SPEAKER_TABLE).ifNotExists()
                .withPartitionKey(CONF_BY_SPEAKER_ATT_SPEAKER_ID, DataTypes.UUID)
                .withClusteringColumn(CONF_ATT_YEAR, DataTypes.INT)
                .withClusteringColumn(CONF_ATT_NAME, DataTypes.TEXT)
                .withColumn(CONF_ATT_LOCATION, DataTypes.TEXT)
                .withColumn(CONF_ATT_LOGO, DataTypes.TEXT)
                .withColumn(CONF_ATT_URL, DataTypes.TEXT)
                .withClusteringOrder(CONF_ATT_YEAR, ClusteringOrder.DESC)
                .withClusteringOrder(CONF_ATT_NAME, ClusteringOrder.ASC)
                .build());
    }
}
