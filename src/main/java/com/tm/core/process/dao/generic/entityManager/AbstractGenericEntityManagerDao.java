package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.scanner.EntityScanner;
import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.process.dao.identifier.QueryService;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGenericEntityManagerDao {
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
