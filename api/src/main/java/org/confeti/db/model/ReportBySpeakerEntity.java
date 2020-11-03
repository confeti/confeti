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
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.confeti.db.model.udt.ReportSourceUDT;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ReportBySpeakerEntity.REPORT_BY_SPEAKER_TABLE)
public class ReportBySpeakerEntity {

    private static final long serialVersionUID = 0L;

    public static final String REPORT_BY_SPEAKER_TABLE = "report_by_speaker";
    public static final String REPORT_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";
    public static final String REPORT_BY_SPEAKER_ATT_YEAR = "year";
    public static final String REPORT_BY_SPEAKER_ATT_TITLE = "title";
    public static final String REPORT_BY_SPEAKER_ATT_COMPLEXITY = "complexity";
    public static final String REPORT_BY_SPEAKER_ATT_CONFERENCE = "conference";
    public static final String REPORT_BY_SPEAKER_ATT_DESCRIPTION = "description";
    public static final String REPORT_BY_SPEAKER_ATT_LANGUAGE = "language";
    public static final String REPORT_BY_SPEAKER_ATT_SOURCE = "source";
    public static final String REPORT_BY_SPEAKER_ATT_TAGS = "tags";

    @PartitionKey
    @CqlName(REPORT_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(1)
    @CqlName(REPORT_BY_SPEAKER_ATT_YEAR)
    private Integer year;

    @ClusteringColumn(2)
    @CqlName(REPORT_BY_SPEAKER_ATT_TITLE)
    private String title;

    @CqlName(REPORT_BY_SPEAKER_ATT_COMPLEXITY)
    private Integer complexity;

    @CqlName(REPORT_BY_SPEAKER_ATT_CONFERENCE)
    private ConferenceShortInfoUDT conference;

    @CqlName(REPORT_BY_SPEAKER_ATT_DESCRIPTION)
    private String description;

    @CqlName(REPORT_BY_SPEAKER_ATT_LANGUAGE)
    private String language;

    @CqlName(REPORT_BY_SPEAKER_ATT_SOURCE)
    private ReportSourceUDT source;

    @CqlName(REPORT_BY_SPEAKER_ATT_TAGS)
    private Set<String> tags;
}
