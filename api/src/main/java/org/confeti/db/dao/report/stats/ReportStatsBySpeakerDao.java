package org.confeti.db.dao.report.stats;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerEntity;

import java.util.UUID;

@Dao
public interface ReportStatsBySpeakerDao {

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerEntity> findBySpeakerId(UUID speakerId);

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerEntity> findBySpeakerIdForYear(UUID speakerId, Integer year);

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerEntity> findBySpeakerIdForConferenceName(UUID speakerId,
                                                                                         Integer year,
                                                                                         String conferenceName);

    @Increment(entityClass = ReportStatsBySpeakerEntity.class)
    ReactiveResultSet incrementReportTotal(UUID speakerId,
                                           Integer year,
                                           String conferenceName,
                                           Long reportTotal);
}
