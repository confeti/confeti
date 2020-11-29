package org.confeti.db.model.speaker;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class AbstractSpeakerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_ATT_ID = "id";
    public static final String SPEAKER_ATT_AVATAR = "avatar";
    public static final String SPEAKER_ATT_NAME = "name";

    @CqlName(SPEAKER_ATT_ID)
    protected UUID id;

    @CqlName(SPEAKER_ATT_NAME)
    protected String name;

    @CqlName(SPEAKER_ATT_AVATAR)
    protected String avatar;

    public abstract UUID getId();
}
