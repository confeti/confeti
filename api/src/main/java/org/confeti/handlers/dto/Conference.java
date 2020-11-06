package org.confeti.handlers.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Data
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Conference implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String name;

    @NotNull
    private Integer year;

    @Nullable
    private String location;

    @Nullable
    private String logo;

    @Nullable
    private String url;

    public static ConferenceBuilder builder(@NotNull final String name,
                                            @NotNull final Integer year) {
        return hiddenBuilder().name(name).year(year);
    }
}
