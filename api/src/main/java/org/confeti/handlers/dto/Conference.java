package org.confeti.handlers.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
