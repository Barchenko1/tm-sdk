package com.tm.core.process.dao.generic.session;

import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.process.dao.identifier.QueryService;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.dao.transaction.TransactionHandler;
import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.scanner.EntityScanner;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import org.hibernate.SessionFactory;

public abstract class AbstractGenericSessionDao {

    protected final SessionFactory sessionFactory;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;
    protected final ITransactionHandler transactionHandler;

    public AbstractGenericSessionDao(SessionFactory sessionFactory,
                                     String entityPackage) {
        this.sessionFactory = sessionFactory;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = initializerQueryService(entityPackage);
        this.transactionHandler = new TransactionHandler(sessionFactory);
    }

    private IQueryService initializerQueryService(String entityPackage) {
        EntityMappingManager entityMappingManager = new EntityMappingManager();
        new EntityScanner(entityMappingManager, entityPackage);
        return new QueryService(entityMappingManager);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
