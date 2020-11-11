package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_CONTACT_INFO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_NAME;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT)
public class SpeakerShortInfoUDT implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_SHORT_INFO_UDT = "speaker_short_info";

    @CqlName(SPEAKER_ATT_ID)
    private UUID id;

    @CqlName(SPEAKER_ATT_CONTACT_INFO)
    private ContactInfoUDT contactInfo;

    @CqlName(SPEAKER_ATT_NAME)
    private String name;

    @NotNull
    public static SpeakerShortInfoUDT from(@NotNull final Speaker speaker) {
        return SpeakerShortInfoUDT.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .contactInfo(ContactInfoUDT.from(speaker.getContactInfo()))
                .build();
    }

    @NotNull
    public static SpeakerShortInfoUDT from(@NotNull final SpeakerFullInfoUDT speakerUDT) {
        return SpeakerShortInfoUDT.builder()
                .id(speakerUDT.getId())
                .name(speakerUDT.getName())
                .contactInfo(speakerUDT.getContactInfo())
                .build();
    }
}
