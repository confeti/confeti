package org.confeti.db.model.company;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.confeti.db.model.BaseEntity;
import org.confeti.service.dto.Company;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static org.confeti.db.model.BaseEntity.updateValue;

@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@CqlName(CompanyEntity.COMPANY_TABLE)
public class CompanyEntity implements BaseEntity<CompanyEntity>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String COMPANY_TABLE = "company";
    public static final String COMPANY_ATT_NAME = "name";
    public static final String COMPANY_ATT_LOGO = "logo";

    @PartitionKey
    @CqlName(COMPANY_ATT_NAME)
    protected String name;

    @CqlName(COMPANY_ATT_LOGO)
    protected String logo;

    @Override
    public void updateFrom(@NotNull final CompanyEntity company) {
        setName(updateValue(name, company.getName()));
        setLogo(updateValue(logo, company.getLogo()));
    }

    @NotNull
    public static CompanyEntity from(@NotNull final CompanyEntity company) {
        return CompanyEntity.builder()
                .name(company.getName())
                .logo(company.getLogo())
                .build();
    }

    @NotNull
    public static CompanyEntity from(@NotNull final Company company) {
        return CompanyEntity.builder()
                .name(company.getName())
                .logo(company.getLogo())
                .build();
    }
}
