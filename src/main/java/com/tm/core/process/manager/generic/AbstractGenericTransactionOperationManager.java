package com.tm.core.process.manager.generic;

import com.tm.core.process.dao.generic.IGenericTransactionDao;

public abstract class AbstractGenericTransactionOperationManager implements IGenericOperationManager{

    protected final IGenericTransactionDao genericTransactionDao;

    public AbstractGenericTransactionOperationManager(IGenericTransactionDao genericTransactionDao) {
        this.genericTransactionDao = genericTransactionDao;
    }

}
