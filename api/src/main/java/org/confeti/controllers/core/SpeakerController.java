package org.confeti.controllers.core;

import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.core.SpeakerStatResponseByConference;
import org.confeti.controllers.dto.core.SpeakerStatResponseByYear;
import org.confeti.service.ReportStatsService;
import org.confeti.service.SpeakerService;
import org.confeti.service.dto.Speaker;
import org.confeti.service.dto.stats.ReportStatsBySpeakerForConference;
import org.confeti.service.dto.stats.ReportStatsBySpeakerForYear;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static org.confeti.controllers.ControllersUtils.CONFERENCE_NAME_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.confeti.controllers.ControllersUtils.SPEAKER_ID_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.SPEAKER_NAME_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.YEAR_URI_PARAMETER;
import static org.confeti.controllers.core.StatisticControllerUtils.handleForAllRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequestWithKey;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/speaker", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SpeakerController {

    private final SpeakerService speakerService;
    private final ReportStatsService reportStatsService;

    @GetMapping
    @ResponseBody
    public Flux<Speaker> handleSpeakersRequest() {
        return speakerService.findAll();
    }

    @GetMapping(params = {CONFERENCE_NAME_URI_PARAMETER})
    @ResponseBody
    public Flux<Speaker> handleSpeakerByConferenceRequest(@RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName) {
        return speakerService.findBy(conferenceName);
    }

    @GetMapping(params = {CONFERENCE_NAME_URI_PARAMETER, YEAR_URI_PARAMETER})
    @ResponseBody
    public Flux<Speaker> handleSpeakersByConferenceYearRequest(
            @RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName,
            @RequestParam(YEAR_URI_PARAMETER) final int year) {
        return speakerService.findBy(conferenceName, year);
    }

    @GetMapping(params = {CONFERENCE_NAME_URI_PARAMETER, YEAR_URI_PARAMETER, SPEAKER_NAME_URI_PARAMETER})
    @ResponseBody
    public Flux<Speaker> handleSpeakerByNameConferenceYear(
            @RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName,
            @RequestParam(YEAR_URI_PARAMETER) final int year,
            @RequestParam(SPEAKER_NAME_URI_PARAMETER) final String speakerName) {
        return speakerService.findBy(conferenceName, year, speakerName);
    }

    @GetMapping(path = "{" + SPEAKER_ID_URI_PARAMETER + "}")
    @ResponseBody
    public Mono<Speaker> handleSpeakersByIdRequest(@PathVariable(SPEAKER_ID_URI_PARAMETER) final UUID speakerId) {
        return speakerService.findBy(speakerId);
    }

    @GetMapping(path = "{" + SPEAKER_ID_URI_PARAMETER + "}/stat/year")
    @ResponseBody
    public Mono<ResponseEntity<Object>> handleStatRequestSpeakerYears(
            @PathVariable(SPEAKER_ID_URI_PARAMETER) final UUID speakerId) {
        return handleSpecifiedRequest(
                reportStatsService.countSpeakerStatsForYear(speakerId),
                ReportStatsBySpeakerForYear::getYear,
                map -> new SpeakerStatResponseByYear()
                        .setId(speakerId)
                        .setYears(map));
    }

    @GetMapping(path = "{" + SPEAKER_ID_URI_PARAMETER + "}/stat/conference")
    @ResponseBody
    public Mono<ResponseEntity<Object>> handleStatRequestSpeakerConferences(
            @PathVariable(SPEAKER_ID_URI_PARAMETER) final UUID speakerId) {
        return handleSpecifiedRequest(
                reportStatsService.countSpeakerStatsForConference(speakerId),
                ReportStatsBySpeakerForConference::getConferenceName,
                map -> new SpeakerStatResponseByConference()
                        .setId(speakerId)
                        .setConferences(map));
    }

    @GetMapping(path = "{" + SPEAKER_ID_URI_PARAMETER + "}/stat", params = {YEAR_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<Object>> handleStatRequestSpeakerYear(
            @PathVariable(SPEAKER_ID_URI_PARAMETER) final UUID speakerID,
            @RequestParam(YEAR_URI_PARAMETER) final int year) {
        return handleSpecifiedRequestWithKey(reportStatsService.countSpeakerStatsForYear(speakerID, year),
                stat -> new SpeakerStatResponseByYear()
                        .setId(stat.getSpeakerId())
                        .setYears(Map.of(stat.getYear(), stat.getReportTotal())));
    }

    @GetMapping(path = "{" + SPEAKER_ID_URI_PARAMETER + "}/stat", params = {CONFERENCE_NAME_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<Object>> handleStatRequestSpeakerYear(
            @PathVariable(SPEAKER_ID_URI_PARAMETER) final UUID speakerID,
            @RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName) {
        return handleSpecifiedRequestWithKey(reportStatsService.countSpeakerStatsForConference(speakerID, conferenceName),
                stat -> new SpeakerStatResponseByConference()
                        .setId(stat.getSpeakerId())
                        .setConferences(Map.of(stat.getConferenceName(), stat.getReportTotal())));
    }

    @GetMapping(path = "/stat/year")
    @ResponseBody
    public Flux<SpeakerStatResponseByYear> handleStatRequestYears() {
        return handleForAllRequest(
                reportStatsService.countSpeakerStatsForYear(),
                ReportStatsBySpeakerForYear::getSpeakerId,
                groupedFlux -> groupedFlux.collectMap(ReportStatsBySpeakerForYear::getYear, ReportStatsBySpeakerForYear::getReportTotal),
                tuple -> new SpeakerStatResponseByYear()
                        .setId(tuple.getT2())
                        .setYears(tuple.getT1()));
    }

    @GetMapping(path = "/stat/conference")
    @ResponseBody
    public Flux<SpeakerStatResponseByConference> handleStatRequestConference() {
        return handleForAllRequest(
                reportStatsService.countSpeakerStatsForConference(),
                ReportStatsBySpeakerForConference::getSpeakerId,
                groupedFlux -> groupedFlux.collectMap(ReportStatsBySpeakerForConference::getConferenceName,
                        ReportStatsBySpeakerForConference::getReportTotal),
                tuple -> new SpeakerStatResponseByConference()
                        .setId(tuple.getT2())
                        .setConferences(tuple.getT1()));
    }
}
