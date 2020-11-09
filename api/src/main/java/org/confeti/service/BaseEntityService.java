package org.confeti.service;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface BaseEntityService<D> {

    Mono<D> upsert(@NotNull D dto);
}
