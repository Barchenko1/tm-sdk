package com.tm.core.process.manager.generic.impl;

import com.tm.core.process.dao.generic.IGenericTransactionDao;
import com.tm.core.process.manager.generic.IGenericTransactionOperationManager;

public abstract class AbstractGenericTransactionOperationManager implements IGenericTransactionOperationManager {

    protected final IGenericTransactionDao genericTransactionDao;

    public AbstractGenericTransactionOperationManager(IGenericTransactionDao genericTransactionDao) {
        this.genericTransactionDao = genericTransactionDao;
    }

}
