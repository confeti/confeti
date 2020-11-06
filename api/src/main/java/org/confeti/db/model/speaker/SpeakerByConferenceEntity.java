package org.confeti.db.model.speaker;

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
import org.confeti.db.model.udt.SpeakerLocationUDT;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(SpeakerByConferenceEntity.SPEAKER_BY_CONFERENCE_TABLE)
public class SpeakerByConferenceEntity extends AbstractSpeakerEntity {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_BY_CONFERENCE_TABLE = "speaker_by_conference";
    public static final String SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME = "conference_name";
    public static final String SPEAKER_BY_CONFERENCE_ATT_YEAR = "year";
    public static final String SPEAKER_BY_CONFERENCE_ATT_LOCATIONS = "locations";

    @PartitionKey
    @CqlName(SPEAKER_BY_CONFERENCE_ATT_CONFERENCE_NAME)
    private String conferenceName;

    @ClusteringColumn(1)
    @CqlName(SPEAKER_BY_CONFERENCE_ATT_YEAR)
    private Integer year;

    @CqlName(SPEAKER_BY_CONFERENCE_ATT_LOCATIONS)
    private Set<SpeakerLocationUDT> locations;

    @ClusteringColumn(2)
    @Override
    public String getName() {
        return name;
    }
}
