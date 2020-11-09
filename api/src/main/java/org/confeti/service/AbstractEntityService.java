package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.confeti.db.dao.BaseDao;
import org.confeti.exception.NotFoundException;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntityService<E, D, R extends BaseDao<E>> implements BaseEntityService<D> {

    protected final R dao;

    @NotNull
    protected Mono<D> upsert(@NotNull final D dto,
                             @NotNull final Function<D, E> dtoToModelConverter,
                             @NotNull final Function<E, D> modelToDtoConverter) {
        return Mono.from(findByPrimaryKey(dto))
                .defaultIfEmpty(dtoToModelConverter.apply(dto))
                .flatMap(e -> Mono.from(dao.upsert(e)).map(rr -> e))
                .map(modelToDtoConverter);
    }

    @NotNull
    protected <T> Mono<D> upsert(@NotNull final Mono<D> savedEntity,
                                 @NotNull final Function<D, Mono<T>> getEntityBy,
                                 @NotNull final BaseDao<T> entityByDao,
                                 @NotNull final Function<T, D> modelToDtoConverter) {
        return Mono.from(savedEntity)
                .flatMap(getEntityBy)
                .flatMap(entityBy -> Mono.from(entityByDao.upsert(entityBy)).map(rr -> entityBy))
                .map(modelToDtoConverter);
    }

    @NotNull
    protected <T> Mono<D> findBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                 @NotNull final Function<T, D> modelToDtoConverter,
                                 @NotNull final NotFoundException notFoundException) {
        return Mono.from(foundEntity)
                .switchIfEmpty(Mono.error(notFoundException))
                .map(modelToDtoConverter);
    }

    @NotNull
    protected abstract MappedReactiveResultSet<E> findByPrimaryKey(@NotNull D dto);
}
