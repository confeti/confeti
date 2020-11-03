package org.confeti.db.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.ConferenceDao;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.ConferenceEntity.CONFERENCE_ATT_LOCATION;
import static org.confeti.db.model.ConferenceEntity.CONFERENCE_ATT_LOGO;
import static org.confeti.db.model.ConferenceEntity.CONFERENCE_ATT_NAME;
import static org.confeti.db.model.ConferenceEntity.CONFERENCE_ATT_URL;
import static org.confeti.db.model.ConferenceEntity.CONFERENCE_ATT_YEAR;
import static org.confeti.db.model.ConferenceEntity.CONFERENCE_TABLE;

@Mapper
public interface ConferenceDaoMapper extends BaseMapper {

    @DaoFactory
    ConferenceDao conferenceDao(@DaoKeyspace CqlIdentifier keyspace);

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
    default void createSchema(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(CONFERENCE_TABLE).ifNotExists()
                .withPartitionKey(CONFERENCE_ATT_NAME, DataTypes.TEXT)
                .withClusteringColumn(CONFERENCE_ATT_YEAR, DataTypes.INT)
                .withColumn(CONFERENCE_ATT_LOCATION, DataTypes.TEXT)
                .withStaticColumn(CONFERENCE_ATT_LOGO, DataTypes.TEXT)
                .withColumn(CONFERENCE_ATT_URL, DataTypes.TEXT)
                .withClusteringOrder(CONFERENCE_ATT_YEAR, ClusteringOrder.DESC)
                .build());
    }
}
