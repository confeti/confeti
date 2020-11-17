package org.confeti.db.model.conference;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.service.dto.Conference;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ConferenceBySpeakerEntity.CONF_BY_SPEAKER_TABLE)
public class ConferenceBySpeakerEntity extends AbstractConferenceEntity {

    private static final long serialVersionUID = 1L;

    public static final String CONF_BY_SPEAKER_TABLE = "conference_by_speaker";
    public static final String CONF_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";

    @PartitionKey
    @CqlName(CONF_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(2)
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    public static ConferenceBySpeakerEntity from(@NotNull final UUID speakerId,
                                                 @NotNull final Conference conference) {
        return ConferenceBySpeakerEntity.builder()
                .speakerId(speakerId)
                .name(conference.getName())
                .year(conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .build();
    }

    @NotNull
    public static ConferenceBySpeakerEntity from(@NotNull final ConferenceBySpeakerEntity conference) {
        return ((ConferenceBySpeakerEntityBuilder<?, ?>) fillCommonFields(conference, builder()))
                .speakerId(conference.getSpeakerId())
                .build();
    }

    @NotNull
    public static ConferenceBySpeakerEntity from(@NotNull final UUID speakerId,
                                                 @NotNull final ConferenceEntity conference) {
        return ((ConferenceBySpeakerEntityBuilder<?, ?>) fillCommonFields(conference, builder()))
                .speakerId(speakerId)
                .build();
    }
}
