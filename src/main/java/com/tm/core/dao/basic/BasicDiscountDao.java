package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.product.IDiscountDao;
import org.hibernate.SessionFactory;

public class BasicDiscountDao extends AbstractDao implements IDiscountDao {
    public BasicDiscountDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
