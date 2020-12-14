package org.confeti.controllers.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.confeti.controllers.dto.Status;
import org.confeti.controllers.dto.core.ConferenceStatResponse;
import org.confeti.controllers.dto.core.InputData;
import org.confeti.service.ConferenceService;
import org.confeti.service.ReportService;
import org.confeti.service.ReportStatsService;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.stats.ReportStatsByConference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.confeti.controllers.ControllersUtils.CONFERENCE_NAME_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.confeti.controllers.ControllersUtils.YEAR_URI_PARAMETER;
import static org.confeti.controllers.core.StatisticControllerUtils.handleForAllRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequestWithKey;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/conference", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ConferenceController {
    private final ConferenceService conferenceService;
    private final ReportStatsService reportStatsService;
    private final ReportService reportService;

    @GetMapping
    @ResponseBody
    public Flux<Conference> handleConferenceRequest() {
        return conferenceService.findAll();
    }

    @GetMapping(path = "{" + CONFERENCE_NAME_URI_PARAMETER + "}/stat", params = {YEAR_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleStatRequestConferenceYear(
            @PathVariable(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName,
            @RequestParam(YEAR_URI_PARAMETER) final int year) {
        return handleSpecifiedRequestWithKey(reportStatsService.countConferenceStatsForYear(conferenceName, year),
                stat -> new ConferenceStatResponse()
                        .setConferenceName(conferenceName)
                        .setYears(Map.of(year, stat.getReportTotal())));
    }

    @GetMapping(path = "{" + CONFERENCE_NAME_URI_PARAMETER + "}/stat")
    @ResponseBody
    public Mono<ResponseEntity<?>> handleStatRequestConference(
            @PathVariable(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName) {
        return handleSpecifiedRequest(reportStatsService.countConferenceStats(conferenceName),
                ReportStatsByConference::getYear,
                map -> new ConferenceStatResponse()
                        .setConferenceName(conferenceName)
                        .setYears(map));
    }

    @GetMapping(path = "/stat")
    @ResponseBody
    public Flux<ConferenceStatResponse> handleStatRequest() {
        return handleForAllRequest(reportStatsService.countConferenceStats(),
                ReportStatsByConference::getConferenceName,
                groupedFlux -> groupedFlux.collectMap(ReportStatsByConference::getYear, ReportStatsByConference::getReportTotal),
                tuple -> new ConferenceStatResponse()
                        .setConferenceName(tuple.getT2())
                        .setYears(tuple.getT1()));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<Status>> handleSaveConference(@RequestBody final InputData inputData) {
        return Mono.justOrEmpty(inputData.getConference())
                .switchIfEmpty(Mono.error(new RuntimeException("Conference couldn't be empty")))
                .flatMap(conferenceService::upsert)
                .thenMany(Flux.fromIterable(Objects.requireNonNullElse(inputData.getReports(), Collections.emptyList())))
                .map(report -> report.setConferences(Set.of(inputData.getConference())))
                .flatMap(reportService::upsert)
                .then(Mono.just(ResponseEntity.ok(Status.SUCCESS)))
                .onErrorResume(e -> {
                    log.error(e.getMessage(), e);
                    return Mono.just(ResponseEntity.badRequest().body(Status.FAIL));
                });
    }
}
