package org.confeti.db.model.conference;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ConferenceEntity.CONFERENCE_TABLE)
public class ConferenceEntity extends AbstractConferenceEntity {

    private static final long serialVersionUID = 1L;

    public static final String CONFERENCE_TABLE = "conference";

    @PartitionKey
    @Override
    public String getName() {
        return name;
    }
}
