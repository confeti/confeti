package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ConferenceBySpeakerEntity;

import java.util.UUID;

@Dao
public interface ConferenceBySpeakerDao extends BaseDao<ConferenceBySpeakerEntity> {

    @Select
    MappedReactiveResultSet<ConferenceBySpeakerEntity> findBySpeakerId(UUID speakerId);

    @Select
    MappedReactiveResultSet<ConferenceBySpeakerEntity> findBySpeakerIdForYear(UUID speakerId,
                                                                              Integer year);
}
