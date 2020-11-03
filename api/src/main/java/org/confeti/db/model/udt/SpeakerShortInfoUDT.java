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
@CqlName(SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT)
public class SpeakerShortInfoUDT implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String SPEAKER_SHORT_INFO_UDT = "conference_short_info";
    public static final String SPEAKER_SHORT_INFO_ATT_CONTACT_INFO = "contact_info";
    public static final String SPEAKER_SHORT_INFO_ATT_NAME = "name";

    @CqlName(SPEAKER_SHORT_INFO_ATT_CONTACT_INFO)
    private ContactInfoUDT contactInfo;

    @CqlName(SPEAKER_SHORT_INFO_ATT_NAME)
    private String name;
}
