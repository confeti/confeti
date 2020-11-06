package org.confeti.db.model.report;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;

import java.util.Set;

import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_SPEAKERS;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportByConferenceEntity.REPORT_BY_CONFERENCE_TABLE)
public class ReportByConferenceEntity extends AbstractReportEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_BY_CONFERENCE_TABLE = "report_by_conference";
    public static final String REPORT_BY_CONFERENCE_ATT_CONFERENCE_NAME = "conference_name";
    public static final String REPORT_BY_CONFERENCE_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(REPORT_BY_CONFERENCE_ATT_CONFERENCE_NAME)
    private String conferenceName;

    @ClusteringColumn(1)
    @CqlName(REPORT_BY_CONFERENCE_ATT_YEAR)
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
}
