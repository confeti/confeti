package org.confeti.db.dao.report;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.speaker.SpeakerEntity;

import java.util.UUID;

@Dao
public interface ReportDao extends BaseDao<ReportEntity> {

    @Select
    MappedReactiveResultSet<ReportEntity> findById(UUID speakerId);
}
