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
public abstract class AbstractEntityService<E extends BaseEntity<E>, D, R extends BaseDao<E>>
        implements BaseEntityService<D> {

    protected final R dao;

    @NotNull
    protected Mono<E> upsert(@NotNull final E entity) {
        return Mono.from(dao.upsert(entity))
                .then(Mono.just(entity));
    }

    @NotNull
    protected Mono<E> upsert(@NotNull final D dto,
                             @NotNull final Function<D, E> dtoToModelConverter) {
        final var entity = dtoToModelConverter.apply(dto);
        return findByPrimaryKey(dto)
                .doOnNext(ce -> ce.updateFrom(entity))
                .defaultIfEmpty(entity)
                .flatMap(this::upsert);
    }

    @NotNull
    protected <T> Mono<D> upsert(@NotNull final Mono<D> savedEntityDto,
                                 @NotNull final Function<D, Mono<T>> getEntityBy,
                                 @NotNull final BaseDao<T> entityByDao) {
        return Mono.from(savedEntityDto)
                .flatMap(entity -> getEntityBy.apply(entity).zipWith(Mono.just(entity)))
                .flatMap(TupleUtils.function((entityBy, dto) ->
                        Mono.from(entityByDao.upsert(entityBy))
                                .then(Mono.just(dto))));
    }

    @NotNull
    protected <T, K> Mono<K> findOneBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                       @NotNull final Function<T, K> modelToDtoConverter) {
        return Mono.from(foundEntity)
                .map(modelToDtoConverter);
    }

    @NotNull
    protected <T, K> Flux<K> findAllBy(@NotNull final MappedReactiveResultSet<T> foundEntity,
                                       @NotNull final Function<T, K> modelToDtoConverter) {
        return Flux.from(foundEntity)
                .map(modelToDtoConverter);
    }

    @NotNull
    protected abstract Mono<E> findByPrimaryKey(@NotNull final D dto);
}
