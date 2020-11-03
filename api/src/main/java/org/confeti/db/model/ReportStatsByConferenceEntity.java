package org.confeti.db.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ReportStatsByConferenceEntity.REPORT_STATS_BY_CONFERENCE_TABLE)
public class ReportStatsByConferenceEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String REPORT_STATS_BY_CONFERENCE_TABLE = "report_stats_by_conference";
    public static final String REPORT_STATS_BY_CONFERENCE_ATT_CONFERENCE_NAME = "conference_name";
    public static final String REPORT_STATS_BY_CONFERENCE_ATT_YEAR = "year";
    public static final String REPORT_STATS_BY_CONFERENCE_ATT_REPORT_TOTAL = "report_total";

    @PartitionKey
    @CqlName(REPORT_STATS_BY_CONFERENCE_ATT_CONFERENCE_NAME)
    private String conferenceName;

    @ClusteringColumn
    @CqlName(REPORT_STATS_BY_CONFERENCE_ATT_YEAR)
    private Integer year;

    @CqlName(REPORT_STATS_BY_CONFERENCE_ATT_REPORT_TOTAL)
    private Long reportTotal;
}