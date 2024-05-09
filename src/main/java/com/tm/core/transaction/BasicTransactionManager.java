package com.tm.core.transaction;

import org.hibernate.SessionFactory;

public class BasicTransactionManager extends AbstractTransactionManager{
    public BasicTransactionManager(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
