package org.confeti.db.dao;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.model.ReportByConferenceEntity;

@Dao
public interface ReportByConferenceDao extends BaseDao<ReportByConferenceEntity> {

    @Select
    MappedReactiveResultSet<ReportByConferenceEntity> findByConferenceName(String conferenceName);
}
