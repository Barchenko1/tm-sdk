package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.product.IProductTypeDao;
import org.hibernate.SessionFactory;

public class BasicProductTypeDao extends AbstractDao implements IProductTypeDao {
    public BasicProductTypeDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
