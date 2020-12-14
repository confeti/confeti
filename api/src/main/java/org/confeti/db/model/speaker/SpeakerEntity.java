package org.confeti.db.model.speaker;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.BaseEntity;
import org.confeti.db.model.udt.ContactInfoUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

import static org.confeti.util.EntityUtil.convertValue;
import static org.confeti.util.EntityUtil.updateValue;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(SpeakerEntity.SPEAKER_TABLE)
public class SpeakerEntity extends AbstractSpeakerEntity implements BaseEntity<SpeakerEntity>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_TABLE = "speaker";
    public static final String SPEAKER_NAME_INDEX = "speaker_name_idx";
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

    @ClusteringColumn
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void updateFrom(@NotNull final SpeakerEntity speaker) {
        final var newContactInfo = speaker.getContactInfo() == null
                ? null
                : ContactInfoUDT.from(speaker.getContactInfo());
        if (newContactInfo != null && contactInfo != null) {
            newContactInfo.setLocation(updateValue(contactInfo.getLocation(), newContactInfo.getLocation()));
            newContactInfo.setEmail(updateValue(contactInfo.getEmail(), newContactInfo.getEmail()));
            newContactInfo.setTwitterUsername(
                    updateValue(contactInfo.getTwitterUsername(), newContactInfo.getTwitterUsername()));
            if (contactInfo.getCompanies() != null) {
                newContactInfo.addCompanies(contactInfo.getCompanies());
            }
        }
        setName(updateValue(name, speaker.getName()));
        setBio(updateValue(bio, speaker.getBio()));
        setAvatar(updateValue(avatar, speaker.getAvatar()));
        setContactInfo(updateValue(contactInfo, newContactInfo));
    }

    @NotNull
    public static SpeakerEntity from(@NotNull final Speaker speaker) {
        return SpeakerEntity.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .contactInfo(convertValue(speaker.getContactInfo(), ContactInfoUDT::from))
                .build();
    }

    @NotNull
    public static SpeakerEntity from(@NotNull final SpeakerEntity speaker) {
        return SpeakerEntity.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .contactInfo(convertValue(speaker.getContactInfo(), ContactInfoUDT::from))
                .build();
    }

    @NotNull
    public static SpeakerEntity from(@NotNull final SpeakerByConferenceEntity speaker) {
        return SpeakerEntity.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .avatar(speaker.getAvatar())
                .build();
    }

    @NotNull
    public static SpeakerEntity from(@NotNull final SpeakerFullInfoUDT speakerUDT) {
        return SpeakerEntity.builder()
                .id(speakerUDT.getId())
                .name(speakerUDT.getName())
                .avatar(speakerUDT.getAvatar())
                .bio(speakerUDT.getBio())
                .contactInfo(convertValue(speakerUDT.getContactInfo(), ContactInfoUDT::from))
                .build();
    }

    @NotNull
    public static SpeakerEntity from(@NotNull final SpeakerShortInfoUDT speakerUDT) {
        return SpeakerEntity.builder()
                .id(speakerUDT.getId())
                .name(speakerUDT.getName())
                .contactInfo(convertValue(speakerUDT.getContactInfo(), ContactInfoUDT::from))
                .build();
    }
}
