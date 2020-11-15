package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.service.dto.Conference;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import static org.confeti.db.model.conference.ConferenceEntity.CONFERENCE_ATT_LOGO;
import static org.confeti.db.model.conference.ConferenceEntity.CONFERENCE_ATT_NAME;
import static org.confeti.db.model.conference.ConferenceEntity.CONFERENCE_ATT_YEAR;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(ConferenceShortInfoUDT.CONFERENCE_SHORT_INFO_UDT)
public class ConferenceShortInfoUDT implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONFERENCE_SHORT_INFO_UDT = "conference_short_info";

    @CqlName(CONFERENCE_ATT_LOGO)
    private String logo;

    @CqlName(CONFERENCE_ATT_NAME)
    private String name;

    @CqlName(CONFERENCE_ATT_YEAR)
    private Integer year;

    @NotNull
    public static ConferenceShortInfoUDT from(@NotNull final Conference conference) {
        return ConferenceShortInfoUDT.builder()
                .logo(conference.getLogo())
                .name(conference.getName())
                .year(conference.getYear())
                .build();
    }

    @NotNull
    public static ConferenceShortInfoUDT from(@NotNull final ConferenceShortInfoUDT conference) {
        return ConferenceShortInfoUDT.builder()
                .logo(conference.getLogo())
                .name(conference.getName())
                .year(conference.getYear())
                .build();
    }
}
