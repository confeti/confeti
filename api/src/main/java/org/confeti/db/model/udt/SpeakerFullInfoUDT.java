package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_BIO;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(SpeakerFullInfoUDT.SPEAKER_FULL_INFO_UDT)
public class SpeakerFullInfoUDT extends SpeakerShortInfoUDT {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_FULL_INFO_UDT = "speaker_short_info";

    @CqlName(SPEAKER_ATT_AVATAR)
    private String avatar;

    @CqlName(SPEAKER_ATT_BIO)
    private String bio;
}
