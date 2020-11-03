package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(ConferenceShortInfoUDT.CONFERENCE_SHORT_INFO_UDT)
public class ConferenceShortInfoUDT implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String CONFERENCE_SHORT_INFO_UDT = "conference_short_info";
    public static final String CONFERENCE_SHORT_INFO_ATT_LOGO = "logo";
    public static final String CONFERENCE_SHORT_INFO_ATT_NAME = "name";
    public static final String CONFERENCE_SHORT_INFO_ATT_YEAR = "year";

    @CqlName(CONFERENCE_SHORT_INFO_ATT_LOGO)
    private String logo;

    @CqlName(CONFERENCE_SHORT_INFO_ATT_NAME)
    private String name;

    @CqlName(CONFERENCE_SHORT_INFO_ATT_YEAR)
    private Integer year;
}
