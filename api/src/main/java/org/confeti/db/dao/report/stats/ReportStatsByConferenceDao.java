package org.confeti.db.dao.report.stats;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.report.stats.ReportStatsByConferenceEntity;

@Dao
public interface ReportStatsByConferenceDao {

    @Select
    MappedReactiveResultSet<ReportStatsByConferenceEntity> findAll();

    @Select
    MappedReactiveResultSet<ReportStatsByConferenceEntity> findByConferenceName(String conferenceName);

    @Select
    MappedReactiveResultSet<ReportStatsByConferenceEntity> findByConferenceNameByYear(String conferenceName, Integer year);

    @Increment(entityClass = ReportStatsByConferenceEntity.class)
    ReactiveResultSet incrementReportTotal(String conferenceName, Integer year, Long reportTotal);
}
