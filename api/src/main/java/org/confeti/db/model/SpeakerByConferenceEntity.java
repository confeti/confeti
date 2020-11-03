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
import org.confeti.db.model.udt.SpeakerLocationUDT;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_TABLE)
public class SpeakerByConferenceEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String SPEAKER_BY_CONFERENCE_TABLE = "speaker_by_conference";
    public static final String SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME = "conference_name";
    public static final String SPEAKER_BY_CONFERENCE_ATT_YEAR = "year";
    public static final String SPEAKER_BY_CONFERENCE_ATT_NAME = "name";
    public static final String SPEAKER_BY_CONFERENCE_ATT_AVATAR = "avatar";
    public static final String SPEAKER_BY_CONFERENCE_ATT_LOCATION = "location";
    public static final String SPEAKER_BY_CONFERENCE_ATT_SPEAKER_ID = "speaker_id";

    @PartitionKey
    @CqlName(SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME)
    private String nameConference;

    @ClusteringColumn(1)
    @CqlName(SPEAKER_BY_CONFERENCE_ATT_YEAR)
    private Integer year;

    @ClusteringColumn(2)
    @CqlName(SPEAKER_BY_CONFERENCE_ATT_NAME)
    private String name;

    @CqlName(SPEAKER_BY_CONFERENCE_ATT_AVATAR)
    private String avatar;

    @CqlName(SPEAKER_BY_CONFERENCE_ATT_LOCATION)
    private SpeakerLocationUDT location;

    @CqlName(SPEAKER_BY_CONFERENCE_ATT_SPEAKER_ID)
    private UUID speakerId;
}
