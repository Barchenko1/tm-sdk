package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.business.IShopDao;
import org.hibernate.SessionFactory;

public class BasicShopDao extends AbstractDao implements IShopDao {
    public BasicShopDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
