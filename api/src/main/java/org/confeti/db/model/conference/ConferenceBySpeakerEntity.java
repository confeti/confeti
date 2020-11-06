package org.confeti.db.model.conference;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ConferenceBySpeakerEntity.CONFERENCE_BY_SPEAKER_TABLE)
public class ConferenceBySpeakerEntity extends AbstractConferenceEntity {

    private static final long serialVersionUID = 1L;

    public static final String CONFERENCE_BY_SPEAKER_TABLE = "conference_by_speaker";
    public static final String CONFERENCE_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";

    @PartitionKey
    @CqlName(CONFERENCE_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(2)
    @Override
    public String getName() {
        return name;
    }
}
