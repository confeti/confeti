package org.confeti.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum Status {
    SUCCESS("success"),
    FAIL("fail");

    @JsonProperty("status")
    private final String value;

    Status(final String value) {
        this.value = value;
    }
}
