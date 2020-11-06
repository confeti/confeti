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
import org.confeti.db.model.udt.ConferenceShortInfoUDT;

import java.util.Set;
import java.util.UUID;

import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_CONFERENCES;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_DESCRIPTION;
import static org.confeti.db.model.report.ReportEntity.REPORT_ATT_TAGS;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportBySpeakerEntity.REPORT_BY_SPEAKER_TABLE)
public class ReportBySpeakerEntity extends AbstractReportEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_BY_SPEAKER_TABLE = "report_by_speaker";
    public static final String REPORT_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";
    public static final String REPORT_BY_SPEAKER_ATT_YEAR = "year";

    @PartitionKey
    @CqlName(REPORT_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(1)
    @CqlName(REPORT_BY_SPEAKER_ATT_YEAR)
    private Integer year;

    @CqlName(REPORT_ATT_CONFERENCES)
    private Set<ConferenceShortInfoUDT> conferences;

    @CqlName(REPORT_ATT_DESCRIPTION)
    private String description;

    @CqlName(REPORT_ATT_TAGS)
    private Set<String> tags;

    @ClusteringColumn(2)
    @Override
    public String getTitle() {
        return title;
    }
}
