package org.confeti.db.dao;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ReportStatsBySpeakerEntity;

import java.util.UUID;

@Dao
public interface ReportStatsBySpeakerDao {

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerEntity> findBySpeakerId(UUID speakerId);

    @Increment(entityClass = ReportStatsBySpeakerEntity.class)
    ReactiveResultSet incrementReportTotal(UUID speakerId,
                                           Integer year,
                                           String conferenceName,
                                           Long reportTotal);
}
