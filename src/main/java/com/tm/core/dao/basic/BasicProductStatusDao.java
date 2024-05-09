package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.product.IProductStatusDao;
import org.hibernate.SessionFactory;

public class BasicProductStatusDao extends AbstractDao implements IProductStatusDao {
    public BasicProductStatusDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
