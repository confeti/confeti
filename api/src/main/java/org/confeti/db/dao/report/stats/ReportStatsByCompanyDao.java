package org.confeti.db.dao.report.stats;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.report.stats.ReportStatsByCompanyEntity;

@Dao
public interface ReportStatsByCompanyDao {

    @Select
    MappedReactiveResultSet<ReportStatsByCompanyEntity> findAll();

    @Select
    MappedReactiveResultSet<ReportStatsByCompanyEntity> findByCompanyName(String companyName);

    @Select
    MappedReactiveResultSet<ReportStatsByCompanyEntity> findByCompanyNameForYear(String companyName, Integer year);

    @Increment(entityClass = ReportStatsByCompanyEntity.class)
    ReactiveResultSet incrementReportTotal(String companyName, Integer year, Long reportTotal);
}
