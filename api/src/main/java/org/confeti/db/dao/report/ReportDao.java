package org.confeti.db.dao.report;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.report.ReportEntity;

import java.util.UUID;

import static org.confeti.db.model.report.AbstractReportEntity.REPORT_ATT_TITLE;

@Dao
public interface ReportDao extends BaseDao<ReportEntity> {

    @Select
    MappedReactiveResultSet<ReportEntity> findAll();

    @Select
    MappedReactiveResultSet<ReportEntity> findById(UUID speakerId);

    @Select(customWhereClause = REPORT_ATT_TITLE + "= :title", allowFiltering = true)
    MappedReactiveResultSet<ReportEntity> findByTitle(String title);
}
