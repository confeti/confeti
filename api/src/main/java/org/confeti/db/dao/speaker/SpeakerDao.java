package org.confeti.db.dao.speaker;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.dao.BaseEntityDao;
import org.confeti.db.model.speaker.SpeakerEntity;

import java.util.UUID;

import static org.confeti.db.model.speaker.AbstractSpeakerEntity.SPEAKER_ATT_NAME;

@Dao
public interface SpeakerDao extends BaseEntityDao<SpeakerEntity> {

    @Select
    MappedReactiveResultSet<SpeakerEntity> findById(UUID speakerId);

    @Select(customWhereClause = SPEAKER_ATT_NAME + "= :name", allowFiltering = true)
    MappedReactiveResultSet<SpeakerEntity> findByName(String name);
}
