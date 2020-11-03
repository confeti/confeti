package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ReportBySpeakerEntity;

import java.util.UUID;

@Dao
public interface ReportBySpeakerDao extends BaseDao<ReportBySpeakerEntity> {

    @Select
    MappedReactiveResultSet<ReportBySpeakerEntity> findBySpeakerId(UUID speakerId);
}
