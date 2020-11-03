package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.SpeakerEntity;

import java.util.UUID;

@Dao
public interface SpeakerDao extends BaseDao<SpeakerEntity> {

    @Select
    MappedReactiveResultSet<SpeakerEntity> findById(UUID speakerId);
}
