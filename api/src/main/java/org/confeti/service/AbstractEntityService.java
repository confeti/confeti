package org.confeti.service;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.BaseEntity;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntityService<E extends BaseEntity, D, R extends BaseDao<E>>
        implements BaseEntityService<D> {

    protected final R dao;

    @NotNull
    protected Mono<E> upsert(@NotNull final E entity) {
        return Mono.from(dao.upsert(entity))
                .then(Mono.just(entity));
    }

    @NotNull
    protected <T> Mono<E> upsert(@NotNull final Mono<E> savedEntity,
                                 @NotNull final Function<E, Mono<T>> getEntityBy,
                                 @NotNull final BaseDao<T> entityByDao) {
        return Mono.from(savedEntity)
                .flatMap(entity -> getEntityBy.apply(entity).zipWith(Mono.just(entity)))
                .flatMap(TupleUtils.function((entityBy, dto) ->
                        Mono.from(entityByDao.upsert(entityBy))
                                .then(Mono.just(dto))));
    }

    @NotNull
    protected <T> Mono<D> findOneBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                    @NotNull final Function<T, D> modelToDtoConverter) {
        return Mono.from(foundEntity)
                .map(modelToDtoConverter);
    }

    @NotNull
    protected <T> Flux<D> findAllBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                    @NotNull final Function<T, D> modelToDtoConverter) {
        return Flux.from(foundEntity)
                .map(modelToDtoConverter);
    }

    @NotNull
    protected abstract MappedReactiveResultSet<E> findByPrimaryKey(@NotNull final D dto);
}
