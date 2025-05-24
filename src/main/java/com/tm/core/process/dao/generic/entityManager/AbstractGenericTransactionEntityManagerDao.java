package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.process.dao.transaction.EntityManagerTransactionHandler;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import jakarta.persistence.EntityManager;

public abstract class AbstractGenericTransactionEntityManagerDao extends AbstractGenericEntityManagerDao {

    protected final ITransactionHandler transactionHandler;

    public AbstractGenericTransactionEntityManagerDao(EntityManager entityManager,
                                                      String entityPackage) {
        super(entityManager, entityPackage);
        this.transactionHandler = new EntityManagerTransactionHandler(entityManager);
    }

}
