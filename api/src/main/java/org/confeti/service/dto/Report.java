package org.confeti.service.dto;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.confeti.db.model.report.AbstractReportEntity;
import org.confeti.db.model.report.ReportByCompanyEntity;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.udt.ComplexityUDT;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.confeti.util.EntityUtil.convertValue;

@Accessors(chain = true)
@Data
@Builder(builderMethodName = "hiddenBuilder")
@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    private String title;

    private Complexity complexity;

    @Builder.Default
    private Set<Conference> conferences = Sets.newHashSet();

    private String description;

    private String language;

    private ReportSource source;

    @Builder.Default
    private Set<Speaker> speakers = Sets.newHashSet();

    @Builder.Default
    private Set<String> tags = Sets.newHashSet();

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
                .source(convertValue(report.getSource(), ReportSource::from))
                .description(report.getDescription())
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .tags(convertValue(report.getTags(), Sets::newHashSet))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportEntity report) {
        return fillCommonFields(report)
                .description(report.getDescription())
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportByConferenceEntity report) {
        return fillCommonFields(report)
                .conferences(Sets.newHashSet(
                        Conference.builder(report.getConferenceName(), report.getYear())
                                .build()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportBySpeakerEntity report) {
        return fillCommonFields(report)
                .description(report.getDescription())
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(Sets.newHashSet(Speaker.builder(report.getSpeakerId()).build()))
                .tags(Sets.newHashSet(report.getTags()))
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
                .tags(Sets.newHashSet(report.getTagName()))
                .build();
    }

    @NotNull
    public static Report from(@NotNull final ReportByCompanyEntity report) {
        return fillCommonFields(report)
                .conferences(report.getConferences().stream()
                        .map(Conference::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(Speaker::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    private static ReportBuilder fillCommonFields(@NotNull final AbstractReportEntity report) {
        return Report.builder(report.getId(), report.getTitle())
                .complexity(convertValue(report.getComplexity(), Complexity::from))
                .language(report.getLanguage())
                .source(convertValue(report.getSource(), ReportSource::from));

    }

    @Accessors(chain = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class ReportSource implements Serializable {

        private static final long serialVersionUID = 1L;

        String presentation;

        String repo;

        String video;

        String talk;

        String article;

        @NotNull
        public static ReportSource from(@NotNull final ReportSourceUDT source) {
            return ReportSource.builder()
                    .presentation(source.getPresentationUrl())
                    .video(source.getVideoUrl())
                    .repo(source.getRepoUrl())
                    .talk(source.getTalkUrl())
                    .article(source.getArticleUrl())
                    .build();
        }

        @NotNull
        public static ReportSource from(@NotNull final ReportSource source) {
            return ReportSource.builder()
                    .presentation(source.getPresentation())
                    .video(source.getVideo())
                    .repo(source.getRepo())
                    .talk(source.getTalk())
                    .article(source.getArticle())
                    .build();
        }
    }

    @Accessors(chain = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Complexity implements Serializable {

        private static final long serialVersionUID = 1L;

        private int value;

        private String description;

        @NotNull
        public static Complexity from(@NotNull final ComplexityUDT complexity) {
            return Complexity.builder()
                    .value(complexity.getValue())
                    .description(complexity.getDescription())
                    .build();
        }

        @NotNull
        public static Complexity from(@NotNull final Complexity complexity) {
            return Complexity.builder()
                    .value(complexity.getValue())
                    .description(complexity.getDescription())
                    .build();
        }
    }
}
