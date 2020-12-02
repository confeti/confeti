package org.confeti.db.dao;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Update;

public interface BaseDao<T> {

    /**
     * Update or insert entity into db. If entity doesn't exist in db
     * and it has null fields then it will not be saved so use {@link #insert(Object)}
     * in this case.
     *
     * @param entity entity will be saved or updated
     * @return ReactiveResultSet
     */
    @Update
    ReactiveResultSet upsert(T entity);

    @Insert
    ReactiveResultSet insert(T entity);
}
