package org.confeti.db.model.report;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.BaseEntity;
import org.confeti.db.model.udt.ReportSourceUDT;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AbstractReportEntity implements BaseEntity {

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

    @CqlName(REPORT_ATT_LANGUAGE)
    protected String language;

    @CqlName(REPORT_ATT_SOURCE)
    protected ReportSourceUDT source;
}
