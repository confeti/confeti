package org.confeti.controllers.dto.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;

import java.util.List;

@Accessors(chain = true)
@Data
@NoArgsConstructor
public class InputData {
    private Conference conference;
    private List<Report> reports;
}
