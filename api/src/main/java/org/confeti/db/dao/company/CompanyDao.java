package org.confeti.db.dao.company;

import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.confeti.db.dao.BaseEntityDao;
import org.confeti.db.model.company.CompanyEntity;

@Dao
public interface CompanyDao extends BaseEntityDao<CompanyEntity> {

    @Select
    MappedReactiveResultSet<CompanyEntity> findByName(String name);
}
