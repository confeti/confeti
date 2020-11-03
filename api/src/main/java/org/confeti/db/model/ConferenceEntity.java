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

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@CqlName(ConferenceEntity.CONFERENCE_TABLE)
public class ConferenceEntity implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String CONFERENCE_TABLE = "conference";
    public static final String CONFERENCE_ATT_NAME = "name";
    public static final String CONFERENCE_ATT_YEAR = "year";
    public static final String CONFERENCE_ATT_LOCATION = "location";
    public static final String CONFERENCE_ATT_LOGO = "logo";
    public static final String CONFERENCE_ATT_URL = "url";

    @PartitionKey
    @CqlName(CONFERENCE_ATT_NAME)
    private String name;

    @ClusteringColumn
    @CqlName(CONFERENCE_ATT_YEAR)
    private Integer year;

    @CqlName(CONFERENCE_ATT_LOCATION)
    private String location;

    @CqlName(CONFERENCE_ATT_LOGO)
    private String logo;

    @CqlName(CONFERENCE_ATT_URL)
    private String url;
}
