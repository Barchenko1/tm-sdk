package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.product.IProductDao;
import org.hibernate.SessionFactory;

public class BasicProductDao extends AbstractDao implements IProductDao {
    public BasicProductDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
