package org.confeti.db.model.speaker;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.udt.ContactInfoUDT;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(SpeakerEntity.SPEAKER_TABLE)
public class SpeakerEntity extends AbstractSpeakerEntity {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_TABLE = "speaker";
    public static final String SPEAKER_ATT_BIO = "bio";
    public static final String SPEAKER_ATT_CONTACT_INFO = "contact_info";

    @CqlName(SPEAKER_ATT_BIO)
    private String bio;

    @CqlName(SPEAKER_ATT_CONTACT_INFO)
    private ContactInfoUDT contactInfo;

    @PartitionKey
    @Override
    public UUID getId() {
        return id;
    }
}
