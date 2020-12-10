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
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;
import static org.confeti.util.EntityUtil.updateValue;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportByCompanyEntity.REPORT_BY_COMPANY_TABLE)
public class ReportByCompanyEntity extends AbstractReportEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_BY_COMPANY_TABLE = "report_by_company";
    public static final String REPORT_BY_COMPANY_ATT_COMPANY_NAME = "company_name";
    public static final String REPORT_BY_COMPANY_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(REPORT_BY_COMPANY_ATT_COMPANY_NAME)
    private String companyName;

    @ClusteringColumn(1)
    @CqlName(REPORT_BY_COMPANY_ATT_YEAR)
    private Integer year;

    @CqlName(REPORT_ATT_CONFERENCES)
    private Set<ConferenceShortInfoUDT> conferences;

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
    public static ReportByCompanyEntity from(@NotNull final String companyName,
                                             @NotNull final Integer year,
                                             @NotNull final Report report) {
        return ReportByCompanyEntity.builder()
                .companyName(companyName)
                .year(year)
                .id(report.getId())
                .title(report.getTitle())
                .complexity(updateValue(report.getComplexity(), ComplexityUDT::from))
                .language(report.getLanguage())
                .source(updateValue(report.getSource(), ReportSourceUDT::from))
                .conferences(updateValue(
                        report.getConferences(),
                        conferences -> conferences.stream()
                                .map(ConferenceShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .speakers(updateValue(
                        report.getSpeakers(),
                        speakers -> speakers.stream()
                                .map(SpeakerShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .tags(updateValue(report.getTags(), Sets::newHashSet))
                .build();
    }

    @NotNull
    public static ReportByCompanyEntity from(@NotNull final String companyName,
                                             @NotNull final Integer year,
                                             @NotNull final ReportEntity report) {
        return ReportByCompanyEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(updateValue(report.getSource(), ReportSourceUDT::from))
                .companyName(companyName)
                .year(year)
                .conferences(updateValue(
                        report.getConferences(),
                        conferences -> conferences.stream()
                                .map(ConferenceShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .speakers(updateValue(
                        report.getSpeakers(),
                        speakers -> speakers.stream()
                                .map(SpeakerShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .tags(updateValue(report.getTags(), Sets::newHashSet))
                .build();
    }

    @NotNull
    public static ReportByCompanyEntity from(@NotNull final ReportByCompanyEntity report) {
        return ReportByCompanyEntity.builder()
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(updateValue(report.getSource(), ReportSourceUDT::from))
                .companyName(report.getCompanyName())
                .year(report.getYear())
                .conferences(updateValue(
                        report.getConferences(),
                        conferences -> conferences.stream()
                                .map(ConferenceShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .speakers(updateValue(
                        report.getSpeakers(),
                        speakers -> speakers.stream()
                                .map(SpeakerShortInfoUDT::from)
                                .collect(Collectors.toSet())))
                .tags(updateValue(report.getTags(), Sets::newHashSet))
                .build();
    }
}
