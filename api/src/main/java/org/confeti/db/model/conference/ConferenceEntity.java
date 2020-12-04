package org.confeti.db.model.conference;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.BaseEntity;
import org.confeti.service.dto.Conference;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static org.confeti.util.EntityUtil.updateValue;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(ConferenceEntity.CONF_TABLE)
public class ConferenceEntity extends AbstractConferenceEntity implements BaseEntity<ConferenceEntity>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONF_TABLE = "conference";

    @PartitionKey
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void updateFrom(@NotNull final ConferenceEntity conference) {
        setName(updateValue(name, conference.getName()));
        setYear(updateValue(year, conference.getYear()));
        setLocation(updateValue(location, conference.getLocation()));
        setLogo(updateValue(logo, conference.getLogo()));
        setUrl(updateValue(url, conference.getUrl()));
    }

    @NotNull
    public static ConferenceEntity from(@NotNull final Conference conference) {
        return ConferenceEntity.builder()
                .name(conference.getName())
                .year(conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .build();
    }

    @NotNull
    public static ConferenceEntity from(@NotNull final ConferenceEntity conference) {
        return ConferenceEntity.builder()
                .name(conference.getName())
                .year(conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .build();
    }

    @NotNull
    public static ConferenceEntity from(@NotNull final ConferenceBySpeakerEntity conference) {
        return ConferenceEntity.builder()
                .name(conference.getName())
                .year(conference.getYear())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .build();
    }
}
