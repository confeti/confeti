package org.confeti.service.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
public class ReportStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private final Long reportTotal;
}
