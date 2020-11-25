package org.confeti.controllers.core;

import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.ErrorResponse;
import org.confeti.controllers.dto.core.TagResponse;
import org.confeti.service.ReportService;
import org.confeti.service.dto.Report;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/report", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReportController {

    private static final String CONFERENCE_NAME_URI_PARAMETER = "conference_name";
    private static final String YEAR_URI_PARAMETER = "year";
    private final ReportService reportService;

    @GetMapping(path = "/tag", params = {YEAR_URI_PARAMETER, CONFERENCE_NAME_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleTagRequest(@RequestParam(YEAR_URI_PARAMETER) final int year,
                                                    @RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName) {
        return countTags(reportService.findBy(conferenceName, year))
                .<ResponseEntity<?>>map(map -> ResponseEntity.ok(new TagResponse(conferenceName, Map.of(year, map))))
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "/tag", params = {CONFERENCE_NAME_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleTagRequest(@RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName) {
        return countTagsByYear(reportService.findBy(conferenceName)
                .groupBy(report -> report.getConferences().iterator().next().getYear()))
                .map(map -> new TagResponse(conferenceName, map))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "/tag")
    @ResponseBody
    public Mono<ResponseEntity<?>> handleTagRequest(
            @RequestParam(value = YEAR_URI_PARAMETER, required = false) final Optional<Integer> year) {
        return reportService.findAll()
                .flatMap(report -> Flux.fromIterable(report.getConferences())
                        .filter(conference -> year.isEmpty() || year.get().equals(conference.getYear()))
                        .zipWith(Mono.just(report)))
                .groupBy(t -> t.getT1().getName())
                .flatMap(group -> countTagsByYear(group.groupBy(t -> t.getT1().getYear(), Tuple2::getT2))
                        .map(map -> new TagResponse(group.key(), map)))
                .collectList()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    private static Mono<Map<String, Long>> countTags(final Flux<Report> reports) {
        return reports.flatMapIterable(report -> Objects.requireNonNullElse(report.getTags(), Collections.emptySet()))
                .groupBy(s -> s)
                .flatMap(group -> Mono.zip(Mono.just(Objects.requireNonNull(group.key())), group.count()))
                .collectMap(Tuple2::getT1, Tuple2::getT2);
    }

    private static Mono<Map<Integer, Map<String, Long>>> countTagsByYear(final Flux<GroupedFlux<Integer, Report>> reports) {
        return reports.flatMap(group -> Mono.zip(Mono.just(Objects.requireNonNull(group.key())), countTags(group)))
                .collectMap(Tuple2::getT1, Tuple2::getT2);
    }
}
