package org.confeti.handlers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private UUID id;

    @NotNull
    private String title;

    @Nullable
    private Complexity complexity;

    @Nullable
    private Set<Conference> conferences;

    @Nullable
    private String description;

    @Nullable
    private String language;

    @Nullable
    private ReportSource source;

    @Nullable
    private Set<Speaker> speakers;

    @Nullable
    private Set<String> tags;

    public static ReportBuilder builder(@NotNull final UUID id,
                                        @NotNull final String title) {
        return hiddenBuilder().id(id).title(title);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class ReportSource implements Serializable {

        private static final long serialVersionUID = 0L;

        String presentation;

        String repo;

        String video;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Complexity {
        INTRODUCTION_TO_TECHNOLOGY(0, "Introduction to technology"),
        FOR_PRACTICING_ENGINEERS(1, "For practicing engineers"),
        HARDCORE(2, "Hardcore"),
        ACADEMIC_TALK(3, "Academic talk");

        private final int value;

        @NotNull
        private final String description;

        Complexity(final int value,
                   @NotNull final String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        @NotNull
        public String getDescription() {
            return description;
        }

        public static Complexity valueOf(final int value) {
            return switch (value) {
                case 0 -> INTRODUCTION_TO_TECHNOLOGY;
                case 1 -> FOR_PRACTICING_ENGINEERS;
                case 2 -> HARDCORE;
                case 3 -> ACADEMIC_TALK;
                default -> throw new IllegalArgumentException("Unexpected value: " + value);
            };
        }
    }
}
