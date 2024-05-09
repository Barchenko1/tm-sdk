package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.org.ITenantConfigDao;
import org.hibernate.SessionFactory;

public class BasicTenantConfigurationDao extends AbstractDao implements ITenantConfigDao {
    public BasicTenantConfigurationDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
