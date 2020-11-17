package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BaseEntityService {

    @NotNull
    static <T, K> Mono<K> findOneBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                    @NotNull final Function<T, K> modelToDtoConverter) {
        return Mono.from(foundEntity)
                .map(modelToDtoConverter);
    }

    @NotNull
    static <T, K> Flux<K> findAllBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                    @NotNull final Function<T, K> modelToDtoConverter) {
        return Flux.from(foundEntity)
                .map(modelToDtoConverter);
    }
}
