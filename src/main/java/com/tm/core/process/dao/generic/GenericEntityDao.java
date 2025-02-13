package com.tm.core.process.dao.generic;

import org.hibernate.SessionFactory;

public class GenericEntityDao extends AbstractGenericEntityDao {
    public GenericEntityDao(SessionFactory sessionFactory, String entityPackage) {
        super(sessionFactory, entityPackage);
    }
}
