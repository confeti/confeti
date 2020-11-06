package org.confeti.db.dao.report;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.report.ReportByTagEntity;

@Dao
public interface ReportByTagDao extends BaseDao<ReportByTagEntity> {

    @Select
    MappedReactiveResultSet<ReportByTagEntity> findByTagName(String tagName);
}
