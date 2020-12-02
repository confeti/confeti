package org.confeti.service;

import org.confeti.db.dao.company.CompanyDao;
import org.confeti.db.model.company.CompanyEntity;
import org.confeti.service.dto.Company;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.confeti.service.BaseEntityService.findMany;

@Service
public class CompanyService extends AbstractEntityService<CompanyEntity, Company, CompanyDao> {

    protected CompanyService(final CompanyDao dao) {
        super(dao);
    }

    @NotNull
    @Override
    public Mono<Company> upsert(@NotNull final Company company) {
        return upsert(company, CompanyEntity::from).map(Company::from);
    }

    @NotNull
    public Flux<Company> findBy(@NotNull final String name) {
        return findMany(dao.findByName(name), Company::from);
    }

    @NotNull
    public Flux<Company> findAll() {
        return findMany(dao.findAll(), Company::from);
    }

    @NotNull
    @Override
    protected Mono<CompanyEntity> findByPrimaryKey(@NotNull final Company company) {
        return Mono.from(dao.findByName(company.getName()));
    }
}
