package org.confeti.db.dao.report.stats;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Increment;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.report.stats.ReportStatsBySpeakerForConferenceEntity;

import java.util.UUID;

@Dao
public interface ReportStatsBySpeakerForConferenceDao {

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerForConferenceEntity> findAll();

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerForConferenceEntity> findBySpeakerId(UUID speakerId);

    @Select
    MappedReactiveResultSet<ReportStatsBySpeakerForConferenceEntity> findBySpeakerIdForConference(UUID speakerId,
                                                                                                  String conferenceName);

    @Increment(entityClass = ReportStatsBySpeakerForConferenceEntity.class)
    ReactiveResultSet incrementReportTotal(UUID speakerId, String conferenceName, Long reportTotal);
}
