package org.confeti.db.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.db.model.udt.ContactInfoUDT;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(SpeakerEntity.SPEAKER_TABLE)
public class SpeakerEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String SPEAKER_TABLE = "speaker";
    public static final String SPEAKER_ATT_ID = "id";
    public static final String SPEAKER_ATT_AVATAR = "avatar";
    public static final String SPEAKER_ATT_BIO = "bio";
    public static final String SPEAKER_ATT_CONTACT_INFO = "contact_info";
    public static final String SPEAKER_ATT_NAME = "name";

    @PartitionKey
    @CqlName(SPEAKER_ATT_ID)
    private UUID id;

    @ClusteringColumn
    @CqlName(SPEAKER_ATT_NAME)
    private String name;

    @CqlName(SPEAKER_ATT_AVATAR)
    private String avatar;

    @CqlName(SPEAKER_ATT_BIO)
    private String bio;

    @CqlName(SPEAKER_ATT_CONTACT_INFO)
    private ContactInfoUDT contactInfo;
}
