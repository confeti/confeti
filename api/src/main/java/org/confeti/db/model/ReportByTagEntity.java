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
import org.confeti.db.model.udt.SpeakerFullInfoUDT;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ReportByTagEntity.REPORT_BY_TAG_TABLE)
public class ReportByTagEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String REPORT_BY_TAG_TABLE = "report_by_tag";
    public static final String REPORT_BY_TAG_ATT_TAG_NAME = "tag_name";
    public static final String REPORT_BY_TAG_ATT_TITLE = "title";
    public static final String REPORT_BY_TAG_ATT_COMPLEXITY = "complexity";
    public static final String REPORT_BY_TAG_ATT_CONFERENCES = "conferences";
    public static final String REPORT_BY_TAG_ATT_LANGUAGE = "language";
    public static final String REPORT_BY_TAG_ATT_REPORT_ID = "report_id";
    public static final String REPORT_BY_TAG_ATT_SOURCE = "source";
    public static final String REPORT_BY_TAG_ATT_SPEAKERS = "speakers";

    @PartitionKey
    @CqlName(REPORT_BY_TAG_ATT_TAG_NAME)
    private String tagName;

    @ClusteringColumn
    @CqlName(REPORT_BY_TAG_ATT_TITLE)
    private String title;

    @CqlName(REPORT_BY_TAG_ATT_COMPLEXITY)
    private Integer complexity;

    @CqlName(REPORT_BY_TAG_ATT_CONFERENCES)
    private Set<ConferenceShortInfoUDT> conferences;

    @CqlName(REPORT_BY_TAG_ATT_LANGUAGE)
    private String language;

    @CqlName(REPORT_BY_TAG_ATT_REPORT_ID)
    private UUID reportId;

    @CqlName(REPORT_BY_TAG_ATT_SOURCE)
    private ReportSourceUDT source;

    @CqlName(REPORT_BY_TAG_ATT_SPEAKERS)
    private Set<SpeakerFullInfoUDT> speakers;
}
