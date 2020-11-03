package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ConferenceEntity;

@Dao
public interface ConferenceDao extends BaseDao<ConferenceEntity> {

    @Select
    MappedReactiveResultSet<ConferenceEntity> findByName(String name);
}
