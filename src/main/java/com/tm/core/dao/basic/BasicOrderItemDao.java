package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.order.IOrderItemDao;
import org.hibernate.SessionFactory;

public class BasicOrderItemDao extends AbstractDao implements IOrderItemDao {
    public BasicOrderItemDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
