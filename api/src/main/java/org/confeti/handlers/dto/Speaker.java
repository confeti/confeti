package org.confeti.handlers.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class ContactInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        String company;

        String email;

        String location;

        String twitterUsername;
    }
}
