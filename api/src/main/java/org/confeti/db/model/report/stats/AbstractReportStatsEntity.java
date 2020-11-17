package org.confeti.db.model.report.stats;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AbstractReportStatsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_STATS_ATT_REPORT_TOTAL = "report_total";

    @CqlName(REPORT_STATS_ATT_REPORT_TOTAL)
    protected Long reportTotal;

    public abstract Long getReportTotal();
}
