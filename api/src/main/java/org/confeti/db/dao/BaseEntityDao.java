package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Select;

public interface BaseEntityDao<T> extends BaseDao<T> {

    @Select
    MappedReactiveResultSet<T> findAll();
}
