package org.confeti.db.dao.report;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseDao;
import org.confeti.db.model.report.ReportByCompanyEntity;

import java.util.UUID;

@Dao
public interface ReportByCompanyDao extends BaseDao<ReportByCompanyEntity> {

    @Select
    MappedReactiveResultSet<ReportByCompanyEntity> findByCompanyName(String companyName);

    @Select
    MappedReactiveResultSet<ReportByCompanyEntity> findByCompanyNameForYear(String companyName, Integer year);

    @Select
    MappedReactiveResultSet<ReportByCompanyEntity> findByTitle(String companyName, Integer year, String title);

    @Select
    MappedReactiveResultSet<ReportByCompanyEntity> findById(String companyName, Integer year, String title, UUID id);
}
