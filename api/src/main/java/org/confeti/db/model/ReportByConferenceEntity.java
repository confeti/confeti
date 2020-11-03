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
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ReportByConferenceEntity.REPORT_BY_CONFERENCE_TABLE)
public class ReportByConferenceEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String REPORT_BY_CONFERENCE_TABLE = "report_by_conference";
    public static final String REPORT_BY_CONFERENCE_ATT_CONFERENCE_NAME = "conference_name";
    public static final String REPORT_BY_CONFERENCE_ATT_YEAR = "year";
    public static final String REPORT_BY_CONFERENCE_ATT_TITLE = "title";
    public static final String REPORT_BY_CONFERENCE_ATT_COMPLEXITY = "complexity";
    public static final String REPORT_BY_CONFERENCE_ATT_LANGUAGE = "language";
    public static final String REPORT_BY_CONFERENCE_ATT_REPORT_ID = "report_id";
    public static final String REPORT_BY_CONFERENCE_ATT_SOURCE = "source";
    public static final String REPORT_BY_CONFERENCE_ATT_SPEAKERS = "speakers";
    public static final String REPORT_BY_CONFERENCE_ATT_TAGS = "tags";

    @PartitionKey
    @CqlName(REPORT_BY_CONFERENCE_ATT_CONFERENCE_NAME)
    private String conferenceName;

    @ClusteringColumn(1)
    @CqlName(REPORT_BY_CONFERENCE_ATT_YEAR)
    private Integer year;

    @ClusteringColumn(2)
    @CqlName(REPORT_BY_CONFERENCE_ATT_TITLE)
    private String title;

    @CqlName(REPORT_BY_CONFERENCE_ATT_COMPLEXITY)
    private Integer complexity;

    @CqlName(REPORT_BY_CONFERENCE_ATT_LANGUAGE)
    private String language;

    @CqlName(REPORT_BY_CONFERENCE_ATT_REPORT_ID)
    private UUID reportId;

    @CqlName(REPORT_BY_CONFERENCE_ATT_SOURCE)
    private ReportSourceUDT source;

    @CqlName(REPORT_BY_CONFERENCE_ATT_SPEAKERS)
    private Set<SpeakerFullInfoUDT> speakers;

    @CqlName(REPORT_BY_CONFERENCE_ATT_TAGS)
    private Set<String> tags;
}
