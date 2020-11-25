package org.confeti.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    public final String message;
    public final Status status = Status.FAIL;
}
