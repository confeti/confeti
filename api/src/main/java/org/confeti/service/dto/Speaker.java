package org.confeti.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.db.model.udt.ContactInfoUDT;
import org.confeti.db.model.udt.SpeakerCompanyUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Speaker implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String name;

    private String avatar;

    private String bio;

    private ContactInfo contactInfo;

    public static SpeakerBuilder builder(@NotNull final UUID id,
                                         @NotNull final String name) {
        return hiddenBuilder().id(id).name(name);
    }

    public static SpeakerBuilder builder(@NotNull final String name) {
        return hiddenBuilder().id(UUID.randomUUID()).name(name);
    }

    @NotNull
    public static Speaker from(@NotNull final Speaker speaker) {
        return Speaker.builder(speaker.getId(), speaker.getName())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .contactInfo(speaker.getContactInfo())
                .build();
    }

    @NotNull
    public static Speaker from(@NotNull final SpeakerEntity speaker) {
        return Speaker.builder(speaker.getId(), speaker.getName())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .contactInfo(ContactInfo.from(speaker.getContactInfo()))
                .build();
    }

    @NotNull
    public static Speaker from(@NotNull final SpeakerByConferenceEntity speaker) {
        return Speaker.builder(speaker.getId(), speaker.getName())
                .avatar(speaker.getAvatar())
                .build();
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
                    .company(company != null ? SpeakerCompany.from(company) : null)
                    .build();
        }

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
        }
    }
}
