package org.confeti.controllers.dto.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private String conferenceName;
    private Map<Integer, Map<String, Long>> years;
}
