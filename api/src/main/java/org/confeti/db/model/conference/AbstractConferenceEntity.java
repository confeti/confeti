package org.confeti.db.model.conference;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AbstractConferenceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONFERENCE_ATT_NAME = "name";
    public static final String CONFERENCE_ATT_YEAR = "year";
    public static final String CONFERENCE_ATT_LOCATION = "location";
    public static final String CONFERENCE_ATT_LOGO = "logo";
    public static final String CONFERENCE_ATT_URL = "url";

    @CqlName(CONFERENCE_ATT_NAME)
    protected String name;

    @ClusteringColumn(1)
    @CqlName(CONFERENCE_ATT_YEAR)
    protected Integer year;

    @CqlName(CONFERENCE_ATT_LOCATION)
    protected String location;

    @CqlName(CONFERENCE_ATT_LOGO)
    protected String logo;

    @CqlName(CONFERENCE_ATT_URL)
    protected String url;
}
