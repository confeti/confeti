package org.confeti.db.mapper.company;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import org.confeti.db.dao.company.CompanyDao;
import org.confeti.db.mapper.BaseMapper;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.confeti.db.model.company.CompanyEntity.COMPANY_ATT_LOGO;
import static org.confeti.db.model.company.CompanyEntity.COMPANY_ATT_NAME;
import static org.confeti.db.model.company.CompanyEntity.COMPANY_TABLE;

@Mapper
public interface CompanyDaoMapper extends BaseMapper {

    @DaoFactory
    CompanyDao companyDao(@DaoKeyspace CqlIdentifier keyspace);

    /**
     * Create the <i>company</i> table.
     *
     * <pre>
     * CREATE TABLE IF NOT EXISTS company (
     *     name text,
     *     logo text,
     *     PRIMARY KEY (name)
     * );
     * </pre>
     */
    default void createCompanyTable(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createTable(COMPANY_TABLE).ifNotExists()
                .withPartitionKey(COMPANY_ATT_NAME, DataTypes.TEXT)
                .withColumn(COMPANY_ATT_LOGO, DataTypes.TEXT)
                .build());
    }
}
