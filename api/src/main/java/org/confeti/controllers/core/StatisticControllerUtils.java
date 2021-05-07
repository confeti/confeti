package org.confeti.controllers.core;

import lombok.extern.slf4j.Slf4j;
import org.confeti.controllers.dto.ErrorResponse;
import org.confeti.service.dto.stats.ReportStats;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.confeti.controllers.ControllersUtils.CONTROLLER_MARKER;

@Slf4j
public final class StatisticControllerUtils {

    // Not instantiable
    private StatisticControllerUtils() {
        throw new AssertionError();
    }

    public static <T extends ReportStats, K> Mono<ResponseEntity<Object>> handleSpecifiedRequest(
            final Flux<T> elements,
            final Function<T, K> keyMapper,
            final Function<Map<K, Long>, ?> responseConverter) {
        return elements
                .collectMap(keyMapper, ReportStats::getReportTotal)
                .doOnSuccess(ign -> log.debug(CONTROLLER_MARKER, "elements collected"))
                .map(responseConverter)
                .doOnSuccess(ign -> log.debug(CONTROLLER_MARKER, "Response converted"))
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .doOnError(StatisticControllerUtils::logError)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    public static <T extends ReportStats> Mono<ResponseEntity<Object>> handleSpecifiedRequestWithKey(
            final Mono<T> element,
            final Function<T, ?> responseConverter) {
        return element
                .map(responseConverter)
                .doOnSuccess(ign -> log.debug(CONTROLLER_MARKER, "Response converted"))
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .doOnError(StatisticControllerUtils::logError)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    public static <T extends ReportStats, K, S, R> Flux<R> handleForAllRequest(
            final Flux<T> elements,
            final Function<? super T, ? extends K> groupMapper,
            final Function<GroupedFlux<? extends K, T>, Mono<S>> groupModifier,
            final Function<Tuple2<S, ? extends K>, R> responseConverter) {
        return elements
                .groupBy(groupMapper)
                .doOnComplete(() -> log.debug(CONTROLLER_MARKER, "Elements grouped"))
                .flatMap(group -> groupModifier.apply(group)
                        .zipWith(Mono.just(Objects.requireNonNull(group.key()))))
                .map(responseConverter)
                .doOnComplete(() -> log.debug(CONTROLLER_MARKER, "Response converted"));
    }

    private static void logError(final Throwable e) {
        log.error(CONTROLLER_MARKER, "ERROR is happen: ", e);
    }

}
