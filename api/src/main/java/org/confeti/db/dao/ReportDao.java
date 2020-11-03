package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ReportEntity;
import org.confeti.db.model.SpeakerEntity;

import java.util.UUID;

@Dao
public interface ReportDao extends BaseDao<ReportEntity> {

    @Select
    MappedReactiveResultSet<SpeakerEntity> findById(UUID speakerId);
}
