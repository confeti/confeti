package org.confeti.db.model.report;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.udt.ComplexityUDT;
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.confeti.service.dto.Report;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_CONFERENCES;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SPEAKERS;
import static org.confeti.util.EntityUtil.convertValue;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportByTagEntity.REPORT_BY_TAG_TABLE)
public class ReportByTagEntity extends AbstractReportEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_BY_TAG_TABLE = "report_by_tag";
    public static final String REPORT_BY_TAG_ATT_TAG_NAME = "tag_name";

    @PartitionKey
    @CqlName(REPORT_BY_TAG_ATT_TAG_NAME)
    private String tagName;

    @Builder.Default
    @CqlName(REPORT_ATT_CONFERENCES)
    private Set<ConferenceShortInfoUDT> conferences = Sets.newHashSet();

    @Builder.Default
    @CqlName(REPORT_ATT_SPEAKERS)
    private Set<SpeakerShortInfoUDT> speakers = Sets.newHashSet();

    @ClusteringColumn(1)
    @Override
    public String getTitle() {
        return title;
    }

    @ClusteringColumn(2)
    @Override
    public UUID getId() {
        return id;
    }

    @NotNull
    public static ReportByTagEntity from(@NotNull final String tagName,
                                         @NotNull final Report report) {
        return ReportByTagEntity.builder()
                .tagName(tagName)
                .id(report.getId())
                .title(report.getTitle())
                .complexity(convertValue(report.getComplexity(), ComplexityUDT::from))
                .language(report.getLanguage())
                .source(convertValue(report.getSource(), ReportSourceUDT::from))
                .conferences(report.getConferences().stream()
                        .map(ConferenceShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static ReportByTagEntity from(@NotNull final String tagName,
                                         @NotNull final ReportEntity report) {
        return ReportByTagEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(convertValue(report.getSource(), ReportSourceUDT::from))
                .tagName(tagName)
                .conferences(report.getConferences().stream()
                        .map(ConferenceShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static ReportByTagEntity from(@NotNull final ReportByTagEntity report) {
        return ReportByTagEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(convertValue(report.getSource(), ReportSourceUDT::from))
                .tagName(report.getTagName())
                .conferences(report.getConferences().stream()
                        .map(ConferenceShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .speakers(report.getSpeakers().stream()
                        .map(SpeakerShortInfoUDT::from)
                        .collect(Collectors.toSet()))
                .build();
    }
}
