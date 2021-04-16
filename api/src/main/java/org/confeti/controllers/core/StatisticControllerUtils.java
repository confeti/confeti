package org.confeti.controllers.core;

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
                .map(responseConverter)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    public static <T extends ReportStats> Mono<ResponseEntity<Object>> handleSpecifiedRequestWithKey(
            final Mono<T> element,
            final Function<T, ?> responseConverter) {
        return element
                .map(responseConverter)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    public static <T extends ReportStats, K, S, R> Flux<R> handleForAllRequest(
            final Flux<T> elements,
            final Function<? super T, ? extends K> groupMapper,
            final Function<GroupedFlux<? extends K, T>, Mono<S>> groupModifier,
            final Function<Tuple2<S, ? extends K>, R> responseConverter) {
        return elements
                .groupBy(groupMapper)
                .flatMap(group -> groupModifier.apply(group)
                        .zipWith(Mono.just(Objects.requireNonNull(group.key()))))
                .map(responseConverter);

    }

}
