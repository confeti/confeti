package org.confeti.service.dto.stats;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public abstract class ReportStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    protected Long reportTotal;
}
