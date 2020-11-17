package org.confeti.db.model.report;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AbstractReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_ATT_ID = "id";
    public static final String REPORT_ATT_TITLE = "title";
    public static final String REPORT_ATT_COMPLEXITY = "complexity";
    public static final String REPORT_ATT_LANGUAGE = "language";
    public static final String REPORT_ATT_SOURCE = "source";

    @CqlName(REPORT_ATT_ID)
    protected UUID id;

    @CqlName(REPORT_ATT_TITLE)
    protected String title;

    @CqlName(REPORT_ATT_COMPLEXITY)
    protected Integer complexity;

    /**
     * An ISO 639 alpha-2 or alpha-3 language code.
     */
    @CqlName(REPORT_ATT_LANGUAGE)
    protected String language;

    @CqlName(REPORT_ATT_SOURCE)
    protected ReportSourceUDT source;

    public abstract UUID getId();

    public abstract String getTitle();

    @NotNull
    protected static AbstractReportEntityBuilder fillCommonFields(@NotNull final AbstractReportEntity report,
                                                                  @NotNull final AbstractReportEntityBuilder<?, ?> builder) {
        return builder
                .id(report.getId())
                .title(report.getTitle())
                .complexity(report.getComplexity())
                .language(report.getLanguage())
                .source(ReportSourceUDT.from(report.getSource()));
    }
}
