package org.confeti.controllers.receiver;

import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.Status;
import org.confeti.controllers.dto.receiver.InputData;
import org.confeti.service.ConferenceService;
import org.confeti.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(REST_API_PATH + "/conference")
@RequiredArgsConstructor
public class ReceiverController {

    private final ReportService reportService;
    private final ConferenceService conferenceService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<Status>> handlePost(@RequestBody final InputData inputData) {
        return Mono.justOrEmpty(inputData.getConference())
                .switchIfEmpty(Mono.error(new RuntimeException("Conference couldn't be empty")))
                .flatMapIterable(ign -> Objects.requireNonNullElse(inputData.getReports(), Collections.emptyList()))
                .map(report -> report.setConferences(Set.of(inputData.getConference())))
                .flatMap(reportService::upsert)
                .hasElements()
                .flatMap(has -> has ? Mono.empty() : conferenceService.upsert(inputData.getConference()))
                .thenReturn(ResponseEntity.ok(Status.SUCCESS))
                .onErrorReturn(ResponseEntity.badRequest().body(Status.FAIL));
    }
}
