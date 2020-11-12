package org.confeti.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public static ReportBuilder builder(@NotNull final UUID id,
                                        @NotNull final String title) {
        return hiddenBuilder().id(id).title(title);
    }

    @NotNull
    public static Report from(@NotNull final Report report) {
        return Report.builder(report.getId(), report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(report.getSource())
                .description(report.getDescription())
                .tags(report.getTags())
                .conferences(report.getConferences())
                .speakers(report.getSpeakers())
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportEntity report) {
        return Report.builder(report.getId(), report.getTitle())
                .complexity(Complexity.valueOf(report.getComplexity()))
                .language(report.getLanguage())
                .source(ReportSource.from(report.getSource()))
                .description(report.getDescription())
                .tags(report.getTags())
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportByConferenceEntity reportEntity) {
        return Report.builder(reportEntity.getId(), reportEntity.getTitle())
                .complexity(Complexity.valueOf(reportEntity.getComplexity()))
                .language(reportEntity.getLanguage())
                .source(ReportSource.from(reportEntity.getSource()))
                .tags(reportEntity.getTags())
                .speakers(reportEntity.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportBySpeakerEntity reportEntity) {
        return Report.builder(reportEntity.getId(), reportEntity.getTitle())
                .complexity(Complexity.valueOf(reportEntity.getComplexity()))
                .language(reportEntity.getLanguage())
                .source(ReportSource.from(reportEntity.getSource()))
                .description(reportEntity.getDescription())
                .tags(reportEntity.getTags())
                .conferences(reportEntity.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportByTagEntity reportEntity) {
        return Report.builder(reportEntity.getId(), reportEntity.getTitle())
                .complexity(Complexity.valueOf(reportEntity.getComplexity()))
                .language(reportEntity.getLanguage())
                .source(ReportSource.from(reportEntity.getSource()))
                .conferences(reportEntity.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(reportEntity.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .build();
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