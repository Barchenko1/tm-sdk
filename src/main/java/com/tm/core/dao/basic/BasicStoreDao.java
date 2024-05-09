package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.business.IStoreDao;
import org.hibernate.SessionFactory;

public class BasicStoreDao extends AbstractDao implements IStoreDao {
    public BasicStoreDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
