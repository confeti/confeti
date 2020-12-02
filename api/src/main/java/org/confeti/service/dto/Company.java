package org.confeti.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.confeti.db.model.company.CompanyEntity;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Accessors(chain = true)
@Data
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String logo;

    public static CompanyBuilder builder(@NotNull final String name) {
        return hiddenBuilder().name(name);
    }

    @NotNull
    public static Company from(@NotNull final Company company) {
        return Company.builder(company.getName())
                .logo(company.getLogo())
                .build();
    }

    @NotNull
    public static Company from(@NotNull final CompanyEntity company) {
        return Company.builder(company.getName())
                .logo(company.getLogo())
                .build();
    }
}
