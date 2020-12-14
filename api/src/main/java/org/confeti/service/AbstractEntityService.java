package org.confeti.service;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.dao.BaseEntityDao;
import org.confeti.db.model.BaseEntity;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntityService<E extends BaseEntity<E>, D, R extends BaseEntityDao<E>>
        implements BaseEntityService {

    protected final R dao;

    @NotNull
    protected Mono<E> insert(@NotNull final E entity) {
        return Mono.from(dao.insert(entity))
                .then(Mono.just(entity));
    }

    @NotNull
    protected Mono<E> upsert(@NotNull final E entity) {
        return Mono.from(dao.upsert(entity))
                .then(Mono.just(entity));
    }

    @NotNull
    protected Mono<E> upsert(@NotNull final D dto,
                             @NotNull final Function<D, E> dtoToModelConverter) {
        final var newEntity = dtoToModelConverter.apply(dto);
        return findByPrimaryKey(dto)
                .doOnNext(foundEntity -> foundEntity.updateFrom(newEntity))
                .flatMap(this::upsert)
                .switchIfEmpty(Mono.from(insert(newEntity)));
    }

    @NotNull
    protected <T> Mono<D> upsert(@NotNull final Mono<D> savedEntityDto,
                                 @NotNull final Function<D, Mono<T>> getEntityBy,
                                 @NotNull final BaseDao<T> entityByDao,
                                 @NotNull final Function<D, Mono<T>> findEntityBy) {
        return Mono.from(savedEntityDto)
                .flatMap(dto -> getEntityBy.apply(dto).zipWith(Mono.just(dto)))
                .flatMap(TupleUtils.function((entityBy, dto) ->
                        Mono.from(findEntityBy.apply(dto)).hasElement()
                                .flatMap(isEntityByDaoExist -> isEntityByDaoExist
                                        ? Mono.from(entityByDao.upsert(entityBy))
                                        : Mono.from(entityByDao.insert(entityBy)))
                                .then(Mono.just(dto))));
    }

    @NotNull
    public abstract Mono<D> upsert(@NotNull final D dto);

    @NotNull
    protected abstract Mono<E> findByPrimaryKey(@NotNull final D dto);

    @NotNull
    protected static <T> Mono<Set<T>> upsertMany(@NotNull final Set<T> entities,
                                                 @NotNull final Function<T, Mono<T>> upsert) {
        return Flux.fromIterable(entities)
                .flatMap(upsert)
                .collectList()
                .map(Sets::newHashSet);
    }
}
