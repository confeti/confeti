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
import java.time.Instant;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(SpeakerCompanyUDT.SPEAKER_COMPANY_UDT)
public class SpeakerCompanyUDT implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String SPEAKER_COMPANY_UDT = "speaker_company";
    public static final String SPEAKER_COMPANY_ATT_ADDED_DATE = "added_date";
    public static final String SPEAKER_COMPANY_ATT_NAME = "name";

    @CqlName(SPEAKER_COMPANY_ATT_ADDED_DATE)
    private Instant addedDate;

    @CqlName(SPEAKER_COMPANY_ATT_NAME)
    private String name;
}
