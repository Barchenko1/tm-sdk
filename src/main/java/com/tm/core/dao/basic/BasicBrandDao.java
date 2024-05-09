package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.product.IBrandDao;
import org.hibernate.SessionFactory;

public class BasicBrandDao extends AbstractDao implements IBrandDao {
    public BasicBrandDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
