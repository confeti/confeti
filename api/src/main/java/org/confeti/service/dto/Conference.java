package org.confeti.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.db.model.conference.ConferenceBySpeakerEntity;
import org.confeti.db.model.conference.ConferenceEntity;
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Conference implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer year;

    private String location;

    private String logo;

    private String url;

    public static ConferenceBuilder builder(@NotNull final String name,
                                            @NotNull final Integer year) {
        return hiddenBuilder().name(name).year(year);
    }

    @NotNull
    public static Conference from(@NotNull final Conference conference) {
        return Conference.builder(conference.getName(), conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .build();
    }

    @NotNull
    public static Conference from(@NotNull final ConferenceEntity conference) {
        return Conference.builder(conference.getName(), conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .build();
    }

    @NotNull
    public static Conference from(@NotNull final ConferenceBySpeakerEntity conferenceEntity) {
        return Conference.builder(conferenceEntity.getName(), conferenceEntity.getYear())
                .location(conferenceEntity.getLocation())
                .logo(conferenceEntity.getLogo())
                .url(conferenceEntity.getUrl())
                .build();
    }

    @NotNull
    public static Conference from(@NotNull final ConferenceShortInfoUDT conferenceUDT) {
        return Conference.builder(conferenceUDT.getName(), conferenceUDT.getYear())
                .logo(conferenceUDT.getLogo())
                .build();
    }
}
