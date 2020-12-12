package org.confeti.controllers.dto.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
@Data
public class ConferenceStatResponse {
    private String conferenceName;
    private Map<Integer, Long> years;
}
