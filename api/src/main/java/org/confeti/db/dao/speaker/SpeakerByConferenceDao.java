package org.confeti.db.dao.speaker;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;

import java.util.UUID;

@Dao
public interface SpeakerByConferenceDao extends BaseDao<SpeakerByConferenceEntity> {

    @Select
    MappedReactiveResultSet<SpeakerByConferenceEntity> findByConferenceName(String conferenceName);

    @Select
    MappedReactiveResultSet<SpeakerByConferenceEntity> findByConferenceNameForYear(String conferenceName, Integer year);

    @Select
    MappedReactiveResultSet<SpeakerByConferenceEntity> findByName(String conferenceName, Integer year, String name);

    @Select
    MappedReactiveResultSet<SpeakerByConferenceEntity> findById(String conferenceName, Integer year, String name, UUID id);
}
