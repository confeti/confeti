package org.confeti.service.dto.stats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.report.stats.ReportStatsByCompanyEntity;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ReportStatsByCompany extends ReportStats {

    private static final long serialVersionUID = 1L;

    private String companyName;

    private Integer year;

    @NotNull
    public static ReportStatsByCompany from(@NotNull final ReportStatsByCompany stats) {
        return ReportStatsByCompany.builder()
                .companyName(stats.getCompanyName())
                .year(stats.getYear())
                .reportTotal(stats.getReportTotal())
                .build();
    }

    @NotNull
    public static ReportStatsByCompany from(@NotNull final ReportStatsByCompanyEntity stats) {
        return ReportStatsByCompany.builder()
                .companyName(stats.getCompanyName())
                .year(stats.getYear())
                .reportTotal(stats.getReportTotal())
                .build();
    }
}
