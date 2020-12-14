package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_BIO;
import static org.confeti.util.EntityUtil.convertValue;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(SpeakerFullInfoUDT.SPEAKER_FULL_INFO_UDT)
public class SpeakerFullInfoUDT extends SpeakerShortInfoUDT {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_FULL_INFO_UDT = "speaker_full_info";

    @CqlName(SPEAKER_ATT_AVATAR)
    private String avatar;

    @CqlName(SPEAKER_ATT_BIO)
    private String bio;

    @NotNull
    public static SpeakerFullInfoUDT from(@NotNull final Speaker speaker) {
        return SpeakerFullInfoUDT.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .contactInfo(convertValue(speaker.getContactInfo(), ContactInfoUDT::from))
                .build();
    }

    @NotNull
    public static SpeakerFullInfoUDT from(@NotNull final SpeakerShortInfoUDT speakerUDT) {
        return SpeakerFullInfoUDT.builder()
                .id(speakerUDT.getId())
                .name(speakerUDT.getName())
                .contactInfo(convertValue(speakerUDT.getContactInfo(), ContactInfoUDT::from))
                .build();
    }

    @NotNull
    public static SpeakerFullInfoUDT from(@NotNull final SpeakerFullInfoUDT speakerUDT) {
        return SpeakerFullInfoUDT.builder()
                .id(speakerUDT.getId())
                .name(speakerUDT.getName())
                .avatar(speakerUDT.getAvatar())
                .bio(speakerUDT.getBio())
                .contactInfo(convertValue(speakerUDT.getContactInfo(), ContactInfoUDT::from))
                .build();
    }
}
