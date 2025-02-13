package com.tm.core.process.manager.generic;

import org.hibernate.SessionFactory;

public class GenericEntityOperationManager extends AbstractGenericEntityOperationManager{

    public GenericEntityOperationManager(SessionFactory sessionFactory, String entityPackage) {
        super(sessionFactory, entityPackage);
    }
}
