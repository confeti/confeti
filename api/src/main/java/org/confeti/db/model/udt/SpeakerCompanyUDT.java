package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.service.dto.Speaker.ContactInfo.SpeakerCompany;
import org.jetbrains.annotations.NotNull;

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

    private static final long serialVersionUID = 1L;

    public static final String SPEAKER_COMPANY_UDT = "speaker_company";
    public static final String SPEAKER_COMPANY_ATT_ADDED_DATE = "added_date";
    public static final String SPEAKER_COMPANY_ATT_NAME = "name";
    public static final String SPEAKER_COMPANY_ATT_YEAR = "year";

    /**
     * Timestamp is stored as a number of milliseconds and
     * the sub-millisecond part will be truncated.
     *
     * @see <a href="https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/temporal_types/">
     *     https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/temporal_types/
     *     </a>
     */
    @CqlName(SPEAKER_COMPANY_ATT_ADDED_DATE)
    private Instant addedDate;

    @CqlName(SPEAKER_COMPANY_ATT_NAME)
    private String name;

    @CqlName(SPEAKER_COMPANY_ATT_YEAR)
    private Integer year;

    @NotNull
    public static SpeakerCompanyUDT from(@NotNull final SpeakerCompany company) {
        final var addedDate = company.getAddedDate() == null
                ? Instant.ofEpochMilli(Instant.now().toEpochMilli())
                : company.getAddedDate();
        return SpeakerCompanyUDT.builder()
                .addedDate(addedDate)
                .year(company.getYear())
                .name(company.getName())
                .build();
    }

    @NotNull
    public static SpeakerCompanyUDT from(@NotNull final SpeakerCompanyUDT company) {
        return SpeakerCompanyUDT.builder()
                .addedDate(company.getAddedDate())
                .year(company.getYear())
                .name(company.getName())
                .build();
    }
}
