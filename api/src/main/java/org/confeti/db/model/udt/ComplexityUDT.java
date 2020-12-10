package org.confeti.db.model.udt;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import static org.confeti.service.dto.Report.Complexity;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@SchemaHint(targetElement = UDT)
@CqlName(ComplexityUDT.COMPLEXITY_UDT)
public class ComplexityUDT implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String COMPLEXITY_UDT = "complexity";
    public static final String COMPLEXITY_UDT_ATT_VALUE = "value";
    public static final String COMPLEXITY_UDT_ATT_DESCRIPTION = "description";

    @CqlName(COMPLEXITY_UDT_ATT_VALUE)
    private Integer value;

    @CqlName(COMPLEXITY_UDT_ATT_DESCRIPTION)
    private String description;

    @NotNull
    public static ComplexityUDT from(@NotNull final Complexity complexity) {
        return ComplexityUDT.builder()
                .value(complexity.getValue())
                .description(complexity.getDescription())
                .build();
    }

    @NotNull
    public static ComplexityUDT from(@NotNull final ComplexityUDT complexity) {
        return ComplexityUDT.builder()
                .value(complexity.getValue())
                .description(complexity.getDescription())
                .build();
    }
}
