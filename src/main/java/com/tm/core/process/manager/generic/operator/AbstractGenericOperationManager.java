package com.tm.core.process.manager.generic.operator;

import com.tm.core.process.dao.generic.IGenericDao;
import com.tm.core.process.manager.generic.IGenericOperationManager;

public abstract class AbstractGenericOperationManager implements IGenericOperationManager {

    protected final IGenericDao genericDao;

    public AbstractGenericOperationManager(IGenericDao genericDao) {
        this.genericDao = genericDao;
    }

}
