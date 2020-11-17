package org.confeti.db.dao.report;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.report.ReportBySpeakerEntity;

import java.util.UUID;

@Dao
public interface ReportBySpeakerDao extends BaseDao<ReportBySpeakerEntity> {

    @Select
    MappedReactiveResultSet<ReportBySpeakerEntity> findBySpeakerId(UUID speakerId);

    @Select
    MappedReactiveResultSet<ReportBySpeakerEntity> findBySpeakerIdForYear(UUID speakerId, Integer year);

    @Select
    MappedReactiveResultSet<ReportBySpeakerEntity> findByTitle(UUID speakerId, Integer year, String title);

    @Select
    MappedReactiveResultSet<ReportBySpeakerEntity> findById(UUID speakerId, Integer year, String title, UUID id);
}
