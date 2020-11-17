package org.confeti.service.dto;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.confeti.db.model.report.AbstractReportEntity;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder(builderMethodName = "hiddenBuilder")
@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String title;

    private Complexity complexity;

    private Set<Conference> conferences;

    private String description;

    private String language;

    private ReportSource source;

    private Set<Speaker> speakers;

    private Set<String> tags;

    public boolean compareBySpeakers(@NotNull final Report report) {
        final var otherSpeakers = Sets.newHashSet(speakers);
        otherSpeakers.removeAll(report.getSpeakers());
        return otherSpeakers.isEmpty();
    }

    public static ReportBuilder builder(@NotNull final UUID id,
                                        @NotNull final String title) {
        return hiddenBuilder().id(id).title(title);
    }

    public static ReportBuilder builder(@NotNull final String title) {
        return hiddenBuilder().title(title);
    }

    @NotNull
    public static Report from(@NotNull final Report report) {
        return Report.builder(report.getId(), report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(ReportSource.from(report.getSource()))
                .description(report.getDescription())
                .tags(Sets.newHashSet(report.getTags()))
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportEntity report) {
        return fillCommonFields(report)
                .description(report.getDescription())
                .tags(Sets.newHashSet(report.getTags()))
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportByConferenceEntity report) {
        return fillCommonFields(report)
                .tags(Sets.newHashSet(report.getTags()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportBySpeakerEntity report) {
        return fillCommonFields(report)
                .description(report.getDescription())
                .tags(Sets.newHashSet(report.getTags()))
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportByTagEntity report) {
        return fillCommonFields(report)
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    private static ReportBuilder fillCommonFields(@NotNull final AbstractReportEntity report) {
        return Report.builder(report.getId(), report.getTitle())
                .complexity(Complexity.valueOf(report.getComplexity()))
                .language(report.getLanguage())
                .source(ReportSource.from(report.getSource()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class ReportSource implements Serializable {

        private static final long serialVersionUID = 1L;

        String presentation;

        String repo;

        String video;

        @NotNull
        public static ReportSource from(@NotNull final ReportSourceUDT source) {
            return ReportSource.builder()
                    .presentation(source.getPresentationUrl())
                    .video(source.getVideoUrl())
                    .repo(source.getRepoUrl())
                    .build();
        }

        @NotNull
        public static ReportSource from(@NotNull final ReportSource source) {
            return ReportSource.builder()
                    .presentation(source.getPresentation())
                    .video(source.getVideo())
                    .repo(source.getRepo())
                    .build();
        }
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
