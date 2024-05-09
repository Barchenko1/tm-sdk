package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.order.IOrderDetailDao;
import org.hibernate.SessionFactory;

public class BasicOrderDetailDao extends AbstractDao implements IOrderDetailDao {
    public BasicOrderDetailDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
