package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.scanner.EntityScanner;
import com.tm.core.process.dao.generic.IGenericDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.process.dao.query.QueryService;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.EntityManager;

public abstract class AbstractGenericEntityManagerDao implements IGenericDao {
    protected final EntityManager entityManager;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;

    public AbstractGenericEntityManagerDao(EntityManager entityManager,
                                           String entityPackage) {
        this.entityManager = entityManager;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = initializerQueryService(entityPackage);
    }

    private IQueryService initializerQueryService(String entityPackage) {
        EntityMappingManager entityMappingManager = new EntityMappingManager();
        new EntityScanner(entityMappingManager, entityPackage);
        return new QueryService(entityMappingManager);
    }
}
