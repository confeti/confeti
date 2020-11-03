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

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ConferenceBySpeakerEntity.CONFERENCE_BY_SPEAKER_TABLE)
public class ConferenceBySpeakerEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String CONFERENCE_BY_SPEAKER_TABLE = "conference_by_speaker";
    public static final String CONFERENCE_BY_SPEAKER_ATT_SPEAKER_ID = "speaker_id";
    public static final String CONFERENCE_BY_SPEAKER_ATT_YEAR = "year";
    public static final String CONFERENCE_BY_SPEAKER_ATT_NAME = "name";
    public static final String CONFERENCE_BY_SPEAKER_ATT_LOCATION = "location";
    public static final String CONFERENCE_BY_SPEAKER_ATT_LOGO = "logo";
    public static final String CONFERENCE_BY_SPEAKER_ATT_URL = "url";

    @PartitionKey
    @CqlName(CONFERENCE_BY_SPEAKER_ATT_SPEAKER_ID)
    private UUID speakerId;

    @ClusteringColumn(1)
    @CqlName(CONFERENCE_BY_SPEAKER_ATT_YEAR)
    private Integer year;

    @ClusteringColumn(2)
    @CqlName(CONFERENCE_BY_SPEAKER_ATT_NAME)
    private String name;

    @CqlName(CONFERENCE_BY_SPEAKER_ATT_LOCATION)
    private String location;

    @CqlName(CONFERENCE_BY_SPEAKER_ATT_LOGO)
    private String logo;

    @CqlName(CONFERENCE_BY_SPEAKER_ATT_URL)
    private String url;
}
