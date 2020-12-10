package org.confeti.db.model.report;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.BaseEntity;
import org.confeti.db.model.udt.ComplexityUDT;
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.service.dto.Report;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.confeti.util.EntityUtil.updateValue;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportEntity.REPORT_TABLE)
public class ReportEntity extends AbstractReportEntity implements BaseEntity<ReportEntity>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_TABLE = "report";
    public static final String REPORT_TITLE_INDEX = "report_title_idx";
    public static final String REPORT_ATT_CONFERENCES = "conferences";
    public static final String REPORT_ATT_DESCRIPTION = "description";
    public static final String REPORT_ATT_SPEAKERS = "speakers";
    public static final String REPORT_ATT_TAGS = "tags";

    @CqlName(REPORT_ATT_CONFERENCES)
    private Set<ConferenceShortInfoUDT> conferences;

    @CqlName(REPORT_ATT_DESCRIPTION)
    private String description;

    @CqlName(REPORT_ATT_SPEAKERS)
    private Set<SpeakerFullInfoUDT> speakers;

    @CqlName(REPORT_ATT_TAGS)
    private Set<String> tags;

    @PartitionKey
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void updateFrom(@NotNull final ReportEntity report) {
        setTitle(updateValue(title, report.getTitle()));
        setComplexity(updateValue(complexity, report.getComplexity()));
        setLanguage(updateValue(language, report.getLanguage()));
        setSource(updateValue(source, report.getSource()));
        setDescription(updateValue(description, report.getDescription()));
        setTags(updateValue(tags, Sets.newHashSet(report.getTags())));
        setConferences(updateValue(conferences, report.getConferences().stream()
                .map(ConferenceShortInfoUDT::from)
                .collect(Collectors.toSet())));
        setSpeakers(updateValue(speakers, report.getSpeakers().stream()
                .map(SpeakerFullInfoUDT::from)
                .collect(Collectors.toSet())));
    }

    @NotNull
    public static ReportEntity from(@NotNull final Report report) {
        return ReportEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(updateValue(report.getComplexity(), ComplexityUDT::from))
                .language(report.getLanguage())
                .source(updateValue(report.getSource(), ReportSourceUDT::from))
                .description(report.getDescription())
                .conferences(updateValue(
                        report.getConferences(),
                        conferences -> conferences.stream()
                                .map(ConferenceShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .speakers(updateValue(
                        report.getSpeakers(),
                        speakers -> speakers.stream()
                                .map(SpeakerFullInfoUDT::from)
                                .collect(Collectors.toSet())))
                .tags(updateValue(report.getTags(), Sets::newHashSet))
                .build();
    }

    @NotNull
    public static ReportEntity from(@NotNull final ReportEntity report) {
        return ReportEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(updateValue(report.getSource(), ReportSourceUDT::from))
                .description(report.getDescription())
                .conferences(updateValue(
                        report.getConferences(),
                        conferences -> conferences.stream()
                                .map(ConferenceShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .speakers(updateValue(
                        report.getSpeakers(),
                        speakers -> speakers.stream()
                                .map(SpeakerFullInfoUDT::from)
                                .collect(Collectors.toSet())))
                .tags(updateValue(report.getTags(), Sets::newHashSet))
                .build();
    }
}
