package org.confeti.db.dao;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ReportStatsByConferenceEntity;

@Dao
public interface ReportStatsByConferenceDao {

    @Select
    MappedReactiveResultSet<ReportStatsByConferenceEntity> findByConferenceName(String conferenceName);

    @Increment(entityClass = ReportStatsByConferenceEntity.class)
    ReactiveResultSet incrementReportTotal(String conferenceName,
                                           Integer year,
                                           Long reportTotal);
}
