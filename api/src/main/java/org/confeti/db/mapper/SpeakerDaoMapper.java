package org.confeti.db.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.SpeakerDao;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.SpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.SpeakerEntity.SPEAKER_ATT_BIO;
import static org.confeti.db.model.SpeakerEntity.SPEAKER_ATT_CONTACT_INFO;
import static org.confeti.db.model.SpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.SpeakerEntity.SPEAKER_ATT_NAME;
import static org.confeti.db.model.SpeakerEntity.SPEAKER_TABLE;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_UDT;

@Mapper
public interface SpeakerDaoMapper extends BaseMapper {

    @DaoFactory
    SpeakerDao speakerDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>speaker</i> table.
     *
     * <pre>
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
    default void createSchema(@NotNull final CqlSession cqlSession) {
        createContactInfoUDT(cqlSession);
        cqlSession.execute(createTable(SPEAKER_TABLE).ifNotExists()
                .withPartitionKey(SPEAKER_ATT_ID, DataTypes.UUID)
                .withClusteringColumn(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_AVATAR, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_BIO, DataTypes.TEXT)
                .withColumn(SPEAKER_ATT_CONTACT_INFO, udt(CONTACT_INFO_UDT, true))
                .build());
    }
}
