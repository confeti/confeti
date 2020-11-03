package org.confeti.db.dao;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Update;

public interface BaseDao<T> {

    @Update
    ReactiveResultSet upsert(T entity);

    @Delete
    ReactiveResultSet delete(T entity);
}
