package org.confeti.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.confeti.db.model.speaker.AbstractSpeakerEntity;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.db.model.udt.ContactInfoUDT;
import org.confeti.db.model.udt.SpeakerCompanyUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Accessors(chain = true)
@Data
@Builder(builderMethodName = "hiddenBuilder")
@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Speaker implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    private String name;

    private String avatar;

    private String bio;

    private ContactInfo contactInfo;

    public boolean canBeUpdatedTo(@NotNull final Speaker speaker) {
        if (name != null && !name.equals(speaker.getName()) || contactInfo != null && speaker.getContactInfo() != null) {
            final boolean canUpdateEmail = canBeUpdatedEmail(
                    speaker.getContactInfo().getEmail(), speaker.getContactInfo().getTwitterUsername());
            final boolean canUpdateTwitter = canBeUpdatedTwitterUsername(
                    speaker.getContactInfo().getEmail(), speaker.getContactInfo().getTwitterUsername());
            final boolean emailAndTwitterEqualPastValues = !canUpdateEmail && !canUpdateTwitter;
            return canUpdateEmail ^ canUpdateTwitter || emailAndTwitterEqualPastValues;
        }
        return false;
    }

    boolean canBeUpdatedEmail(@NotNull final String newEmail,
                              @NotNull final String newTwitter) {
        return canBeUpdated(
                contactInfo.getEmail(), newEmail,
                contactInfo.getTwitterUsername(), newTwitter);
    }

    boolean canBeUpdatedTwitterUsername(@NotNull final String newEmail,
                                        @NotNull final String newTwitter) {
        return canBeUpdated(
                contactInfo.getTwitterUsername(), newTwitter,
                contactInfo.getEmail(), newEmail);
    }

    @SuppressWarnings({"PMD.UseObjectForClearerAPI"})
    private boolean canBeUpdated(@Nullable final String oldValue,
                                 @NotNull final String newValue,
                                 @Nullable final String oldDependentValue,
                                 @NotNull final String newDependentValue) {
        return oldDependentValue != null && (oldValue == null && oldDependentValue.equals(newDependentValue)
                || oldValue != null && !oldValue.equals(newValue));
    }

    public static SpeakerBuilder builder(@NotNull final UUID id) {
        return hiddenBuilder().id(id);
    }

    public static SpeakerBuilder builder(@NotNull final UUID id,
                                         @NotNull final String name) {
        return hiddenBuilder().id(id).name(name);
    }

    public static SpeakerBuilder builder(@NotNull final String name) {
        return hiddenBuilder().name(name);
    }

    @NotNull
    public static Speaker from(@NotNull final Speaker speaker) {
        return Speaker.builder(speaker.getId(), speaker.getName())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .contactInfo(ContactInfo.from(speaker.getContactInfo()))
                .build();
    }

    @NotNull
    public static Speaker from(@NotNull final SpeakerEntity speaker) {
        return fillCommonFields(speaker)
                .bio(speaker.getBio())
                .contactInfo(ContactInfo.from(speaker.getContactInfo()))
                .build();
    }

    @NotNull
    public static Speaker from(@NotNull final SpeakerByConferenceEntity speaker) {
        return fillCommonFields(speaker).build();
    }

    @NotNull
    public static Speaker from(@NotNull final SpeakerFullInfoUDT speakerUDT) {
        return Speaker.builder(speakerUDT.getId(), speakerUDT.getName())
                .avatar(speakerUDT.getAvatar())
                .bio(speakerUDT.getBio())
                .contactInfo(ContactInfo.from(speakerUDT.getContactInfo()))
                .build();
    }

    @NotNull
    public static Speaker from(@NotNull final SpeakerShortInfoUDT speakerUDT) {
        return Speaker.builder(speakerUDT.getId(), speakerUDT.getName())
                .contactInfo(ContactInfo.from(speakerUDT.getContactInfo()))
                .build();
    }

    @NotNull
    private static SpeakerBuilder fillCommonFields(@NotNull final AbstractSpeakerEntity speaker) {
        return Speaker.builder(speaker.getId(), speaker.getName())
                .avatar(speaker.getAvatar());
    }

    @NotNull
    public static Speaker updateOrNew(@NotNull final Speaker oldSpeaker,
                                      @NotNull final Speaker newSpeaker) {
        if (oldSpeaker.canBeUpdatedTo(newSpeaker)) {
            final var newEmail = newSpeaker.getContactInfo().getEmail();
            final var newTwitter = newSpeaker.getContactInfo().getTwitterUsername();
            final var canUpdateEmail = oldSpeaker.canBeUpdatedEmail(newEmail, newTwitter);
            final var canUpdateTwitter = oldSpeaker.canBeUpdatedTwitterUsername(newEmail, newTwitter);
            return Speaker.builder(oldSpeaker.getId(), oldSpeaker.getName())
                    .avatar(newSpeaker.getAvatar())
                    .bio(newSpeaker.getBio())
                    .contactInfo(ContactInfo.builder()
                            .company(newSpeaker.getContactInfo().getCompany())
                            .location(newSpeaker.getContactInfo().getLocation())
                            .email(canUpdateEmail ? newEmail : oldSpeaker.getContactInfo().getEmail())
                            .twitterUsername(canUpdateTwitter ? newTwitter : oldSpeaker.getContactInfo().getTwitterUsername())
                            .build())
                    .build();
        }
        newSpeaker.setId(UUID.randomUUID());
        return newSpeaker;
    }

    @Accessors(chain = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class ContactInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        SpeakerCompany company;

        String email;

        String location;

        String twitterUsername;

        @NotNull
        public static ContactInfo from(@NotNull final ContactInfoUDT contactInfoUDT) {
            final var company = contactInfoUDT.getLastSpeakerCompany();
            return ContactInfo.builder()
                    .email(contactInfoUDT.getEmail())
                    .location(contactInfoUDT.getLocation())
                    .twitterUsername(contactInfoUDT.getTwitterUsername())
                    .company(company == null ? null : SpeakerCompany.from(company))
                    .build();
        }

        @NotNull
        public static ContactInfo from(@NotNull final ContactInfo contactInfo) {
            return ContactInfo.builder()
                    .email(contactInfo.getEmail())
                    .location(contactInfo.getLocation())
                    .twitterUsername(contactInfo.getTwitterUsername())
                    .company(SpeakerCompany.from(contactInfo.getCompany()))
                    .build();
        }

        @Accessors(chain = true)
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static final class SpeakerCompany implements Serializable {

            private static final long serialVersionUID = 1L;

            private Instant addedDate;

            private String name;

            private Integer year;

            @NotNull
            public static SpeakerCompany from(@NotNull final SpeakerCompanyUDT companyUDT) {
                return SpeakerCompany.builder()
                        .addedDate(companyUDT.getAddedDate())
                        .year(companyUDT.getYear())
                        .name(companyUDT.getName())
                        .build();
            }

            @NotNull
            public static SpeakerCompany from(@NotNull final SpeakerCompany company) {
                return SpeakerCompany.builder()
                        .addedDate(company.getAddedDate())
                        .year(company.getYear())
                        .name(company.getName())
                        .build();
            }
        }
    }
}
