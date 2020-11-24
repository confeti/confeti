package org.confeti.controllers.core;

import com.datastax.oss.driver.shaded.guava.common.base.Suppliers;
import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.ErrorResponse;
import org.confeti.controllers.dto.core.TagResponse;
import org.confeti.service.ReportService;
import org.confeti.service.dto.Conference;
import org.javatuples.Pair;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
        return reportService.findBy(conferenceName, year)
                .map(report -> Objects.requireNonNullElse(report.getTags(), Collections.<String>emptySet()))
                .collect(Suppliers.ofInstance(new ArrayList<String>()), ArrayList::addAll)
                .map(ReportController::convertCollectionToMap)
                .<ResponseEntity<?>>map(map -> ResponseEntity.ok(new TagResponse(conferenceName, Map.of(year, map))))
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "/tag", params = {CONFERENCE_NAME_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleTagRequest(@RequestParam(CONFERENCE_NAME_URI_PARAMETER) final String conferenceName) {
        return reportService.findBy(conferenceName)
                .collect(Suppliers.ofInstance(new HashMap<Integer, Map<String, Integer>>()), (collection, report) ->
                        report.getConferences().stream()
                                .filter(conference -> conference.getName().equals(conferenceName))
                                .map(Conference::getYear)
                                .forEach(year -> createYearMap(collection, () -> year, report::getTags))
                )
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
                        .collectMap(conference -> conference, ign -> report.getTags()))
                .collect(Suppliers.ofInstance(new HashMap<String, List<Pair<Integer, Set<String>>>>()), (collection, map) ->
                        map.forEach(((conference, strings) -> {
                            collection.computeIfPresent(conference.getName(), (ign, pairList) -> {
                                pairList.add(Pair.with(conference.getYear(), strings));
                                return pairList;
                            });
                            collection.putIfAbsent(conference.getName(),
                                    new ArrayList<>(Collections.singleton(new Pair<>(conference.getYear(), strings))));
                        })))
                .flatMapIterable(HashMap::entrySet)
                .flatMap(entry -> Flux.fromIterable(entry.getValue())
                        .collect(Suppliers.ofInstance(new HashMap<Integer, Map<String, Integer>>()),
                                (collection, pair) -> createYearMap(collection, pair::getValue0, pair::getValue1))
                        .map(map -> new TagResponse(entry.getKey(), map)))
                .collectList()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    private static Map<String, Integer> convertCollectionToMap(final Collection<String> tags) {
        final Map<String, Integer> map = new ConcurrentHashMap<>();
        tags.forEach(tag -> {
            map.computeIfPresent(tag, (t, amount) -> ++amount);
            map.putIfAbsent(tag, 1);
        });
        return map;
    }

    private static void createYearMap(final Map<Integer, Map<String, Integer>> collection,
                                      final Supplier<Integer> yearSupplier,
                                      final Supplier<Set<String>> tagSupplier) {
        collection.computeIfPresent(yearSupplier.get(), (ign, tagsMap) -> {
            convertCollectionToMap(tagSupplier.get())
                    .forEach((tag, amount) -> {
                        tagsMap.computeIfPresent(tag, (t, value) -> value + amount);
                        tagsMap.putIfAbsent(tag, amount);
                    });
            return tagsMap;
        });
        collection.putIfAbsent(yearSupplier.get(), convertCollectionToMap(tagSupplier.get()));
    }
}
