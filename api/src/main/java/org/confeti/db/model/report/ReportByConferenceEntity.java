package org.confeti.db.model.report;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.confeti.service.dto.Report;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SPEAKERS;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportByConferenceEntity.REPORT_BY_CONF_TABLE)
public class ReportByConferenceEntity extends AbstractReportEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_BY_CONF_TABLE = "report_by_conference";
    public static final String REPORT_BY_CONF_ATT_CONF_NAME = "conference_name";
    public static final String REPORT_BY_CONF_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(REPORT_BY_CONF_ATT_CONF_NAME)
    private String conferenceName;

    @ClusteringColumn(1)
    @CqlName(REPORT_BY_CONF_ATT_YEAR)
    private Integer year;

    @CqlName(REPORT_ATT_SPEAKERS)
    private Set<SpeakerShortInfoUDT> speakers;

    @CqlName(REPORT_ATT_TAGS)
    private Set<String> tags;

    @ClusteringColumn(2)
    @Override
    public String getTitle() {
        return title;
    }

    @ClusteringColumn(3)
    @Override
    public UUID getId() {
        return id;
    }

    @NotNull
    public static ReportByConferenceEntity from(@NotNull final String conferenceName,
                                                @NotNull final Integer year,
                                                @NotNull final Report report) {
        return ReportByConferenceEntity.builder()
                .conferenceName(conferenceName)
                .year(year)
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity().getValue())
                .language(report.getLanguage())
                .source(ReportSourceUDT.from(report.getSource()))
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    public static ReportByConferenceEntity from(@NotNull final String conferenceName,
                                                @NotNull final Integer year,
                                                @NotNull final ReportEntity report) {
        return ReportByConferenceEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(ReportSourceUDT.from(report.getSource()))
                .conferenceName(conferenceName)
                .year(year)
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    public static ReportByConferenceEntity from(@NotNull final ReportByConferenceEntity report) {
        return ReportByConferenceEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(ReportSourceUDT.from(report.getSource()))
                .conferenceName(report.getConferenceName())
                .year(report.getYear())
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    public static ReportByConferenceEntity from(@NotNull final String conferenceName,
                                                @NotNull final ReportBySpeakerEntity report) {
        return ReportByConferenceEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(ReportSourceUDT.from(report.getSource()))
                .conferenceName(conferenceName)
                .year(report.getYear())
                .speakers(Sets.newHashSet(
                        SpeakerFullInfoUDT.builder().id(report.getSpeakerId())
                                .build()))
                .tags(Sets.newHashSet(report.getTags()))
                .build();
    }

    @NotNull
    public static ReportByConferenceEntity from(@NotNull final String conferenceName,
                                                @NotNull final Integer year,
                                                @NotNull final ReportByTagEntity report) {
        return ReportByConferenceEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(ReportSourceUDT.from(report.getSource()))
                .conferenceName(conferenceName)
                .year(year)
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .tags(Sets.newHashSet(report.getTagName()))
                .build();
    }
}
