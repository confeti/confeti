package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.confeti.service.dto.Speaker.ContactInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import static org.confeti.util.EntityUtil.convertValue;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(ContactInfoUDT.CONTACT_INFO_UDT)
public class ContactInfoUDT implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONTACT_INFO_UDT = "contact_info";
    public static final String CONTACT_INFO_ATT_COMPANIES = "companies";
    public static final String CONTACT_INFO_ATT_EMAIL = "email";
    public static final String CONTACT_INFO_ATT_LOCATION = "location";
    public static final String CONTACT_INFO_ATT_TWITTER = "twitter_username";

    @Builder.Default
    @CqlName(CONTACT_INFO_ATT_COMPANIES)
    private Set<SpeakerCompanyUDT> companies = Sets.newHashSet();

    @CqlName(CONTACT_INFO_ATT_EMAIL)
    private String email;

    @CqlName(CONTACT_INFO_ATT_LOCATION)
    private String location;

    @CqlName(CONTACT_INFO_ATT_TWITTER)
    private String twitterUsername;

    public void addCompanies(@NotNull final Set<SpeakerCompanyUDT> companiesUDT) {
        final var newCompanies = companiesUDT.stream()
                .map(SpeakerCompanyUDT::from)
                .collect(Collectors.toSet());
        companies.addAll(newCompanies);
    }

    @NotNull
    public static ContactInfoUDT from(@NotNull final ContactInfo contactInfo) {
        final var company = contactInfo.getCompany();
        return ContactInfoUDT.builder()
                .email(contactInfo.getEmail())
                .location(contactInfo.getLocation())
                .twitterUsername(contactInfo.getTwitterUsername())
                .companies(convertValue(
                        company,
                        Sets.newHashSet(),
                        c -> Sets.newHashSet(SpeakerCompanyUDT.from(c))))
                .build();
    }

    @NotNull
    public static ContactInfoUDT from(@NotNull final ContactInfoUDT contactInfo) {
        return ContactInfoUDT.builder()
                .email(contactInfo.getEmail())
                .location(contactInfo.getLocation())
                .twitterUsername(contactInfo.getTwitterUsername())
                .companies(contactInfo.getCompanies().stream()
                        .map(SpeakerCompanyUDT::from)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Nullable
    public SpeakerCompanyUDT getLastSpeakerCompany() {
        return companies.stream()
                .max(Comparator.comparing(SpeakerCompanyUDT::getYear)
                        .thenComparing(SpeakerCompanyUDT::getAddedDate))
                .orElse(null);
    }
}
