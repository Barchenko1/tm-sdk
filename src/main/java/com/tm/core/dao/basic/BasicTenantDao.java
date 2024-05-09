package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.org.ITenantDao;
import org.hibernate.SessionFactory;

public class BasicTenantDao extends AbstractDao implements ITenantDao {
    public BasicTenantDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
