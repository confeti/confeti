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
import java.util.Set;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(ContactInfoUDT.CONTACT_INFO_UDT)
public class ContactInfoUDT implements Serializable {

    private static final long serialVersionUID = 0L;

    public static final String CONTACT_INFO_UDT = "contact_info";
    public static final String CONTACT_INFO_ATT_COMPANIES = "companies";
    public static final String CONTACT_INFO_ATT_EMAIL = "email";
    public static final String CONTACT_INFO_ATT_LOCATIONS = "locations";
    public static final String CONTACT_INFO_ATT_TWITTER_USERNAME = "twitter_username";

    @CqlName(CONTACT_INFO_ATT_COMPANIES)
    private Set<SpeakerCompanyUDT> companies;

    @CqlName(CONTACT_INFO_ATT_EMAIL)
    private String email;

    @CqlName(CONTACT_INFO_ATT_LOCATIONS)
    private Set<SpeakerLocationUDT> locations;

    @CqlName(CONTACT_INFO_ATT_TWITTER_USERNAME)
    private String twitterUsername;
}
