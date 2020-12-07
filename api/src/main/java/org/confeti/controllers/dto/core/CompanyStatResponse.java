package org.confeti.controllers.dto.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
@Data
public class CompanyStatResponse {
    private String companyName;
    private Map<Integer, Long> years;
}
