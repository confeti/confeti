package org.confeti.controllers.core;

import com.datastax.oss.driver.shaded.guava.common.base.Suppliers;
import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.ErrorResponse;
import org.confeti.controllers.dto.core.TagResponse;
import org.confeti.service.ReportService;
import org.confeti.service.dto.Conference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/report", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping(path = "/tag", params = {"year", "conference_name"})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleTagRequest(@RequestParam(value = "year") int year,
                                                    @RequestParam(value = "conference_name") String conferenceName) {
        return reportService.findBy(conferenceName, year)
                .map(report -> Objects.requireNonNullElse(report.getTags(), Collections.<String>emptySet()))
                .collect(Suppliers.ofInstance(new ArrayList<String>()), ArrayList::addAll)
                .map(ReportController::convertTagsRepresentation)
                .<ResponseEntity<?>>map(map -> ResponseEntity.ok(new TagResponse(conferenceName, Map.of(year, map))))
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "/tag", params = {"conference_name"})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleTagRequest(@RequestParam("conference_name") String conferenceName) {
        return reportService.findBy(conferenceName)
                .collect(Suppliers.ofInstance(new HashMap<Integer, Map<String, Integer>>()), (collection, report) ->
                        report.getConferences().stream()
                                .filter(conference -> conference.getName().equals(conferenceName))
                                .map(Conference::getYear)
                                .forEach(year -> {
                                    collection.computeIfPresent(year, (y, tagsMap) -> {
                                        convertTagsRepresentation(report.getTags())
                                                .forEach((tag, amount) -> {
                                                    tagsMap.computeIfPresent(tag, (t, value) -> value + amount);
                                                    tagsMap.putIfAbsent(tag, amount);
                                                });
                                        return tagsMap;
                                    });
                                    collection.putIfAbsent(year, convertTagsRepresentation(report.getTags()));
                                })
                )
                .map(map -> new TagResponse(conferenceName, map))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "/tag", params = {"year"})
    @ResponseBody
    public Flux<ResponseEntity<?>> handleTagRequest(@RequestParam(value = "year", required = false) int year) {
        return Flux.fromIterable(Collections.singleton(ResponseEntity.badRequest().body(new ErrorResponse(""))));
    }

    private static Map<String, Integer> convertTagsRepresentation(final Collection<String> tags) {
        Map<String, Integer> map = new HashMap<>();
        tags.forEach(tag -> {
            map.computeIfPresent(tag, (t, amount) -> ++amount);
            map.putIfAbsent(tag, 1);
        });
        return map;
    }


}
