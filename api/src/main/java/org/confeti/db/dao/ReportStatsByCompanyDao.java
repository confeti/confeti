package org.confeti.db.dao;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ReportStatsByCompanyEntity;

@Dao
public interface ReportStatsByCompanyDao {

    @Select
    MappedReactiveResultSet<ReportStatsByCompanyEntity> findByCompanyName(String companyName);

    @Increment(entityClass = ReportStatsByCompanyEntity.class)
    ReactiveResultSet incrementReportTotal(String companyName,
                                           Integer year,
                                           Long reportTotal);
}
