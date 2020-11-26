package org.confeti.db.model.conference;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AbstractConferenceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONF_ATT_NAME = "name";
    public static final String CONF_ATT_YEAR = "year";
    public static final String CONF_ATT_LOCATION = "location";
    public static final String CONF_ATT_LOGO = "logo";
    public static final String CONF_ATT_URL = "url";

    @CqlName(CONF_ATT_NAME)
    protected String name;

    @ClusteringColumn(1)
    @CqlName(CONF_ATT_YEAR)
    protected Integer year;

    @CqlName(CONF_ATT_LOCATION)
    protected String location;

    @CqlName(CONF_ATT_LOGO)
    protected String logo;

    @CqlName(CONF_ATT_URL)
    protected String url;

    public abstract String getName();

    @NotNull
    protected static AbstractConferenceEntityBuilder<?, ?> fillCommonFields(@NotNull final AbstractConferenceEntity conference,
                                                                            @NotNull final AbstractConferenceEntityBuilder<?, ?> builder) {
        return builder
                .name(conference.getName())
                .year(conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl());
    }
}
