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
import org.confeti.db.model.udt.SpeakerFullInfoUDT;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ReportEntity.REPORT_TABLE)
public class ReportEntity extends AbstractReportEntity {

    private static final long serialVersionUID = 1L;

    public static final String REPORT_TABLE = "report";
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
}
