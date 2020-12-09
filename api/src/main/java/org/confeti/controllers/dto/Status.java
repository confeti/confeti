package org.confeti.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Status {
    SUCCESS("success"),
    FAIL("fail");

    @JsonProperty("status")
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    Status(final String value) {
        this.value = value;
    }
}
