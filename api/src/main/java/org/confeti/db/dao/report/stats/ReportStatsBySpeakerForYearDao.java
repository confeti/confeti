package org.confeti.db.dao.report.stats;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerForYearEntity;

import java.util.UUID;

@Dao
public interface ReportStatsBySpeakerForYearDao {

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerForYearEntity> findBySpeakerId(UUID speakerId);

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerForYearEntity> findBySpeakerIdForYear(UUID speakerId, Integer year);

    @Increment(entityClass = ReportStatsBySpeakerForYearEntity.class)
    ReactiveResultSet incrementReportTotal(UUID speakerId, Integer year, Long reportTotal);
}
