package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.order.IOrderStatusDao;
import org.hibernate.SessionFactory;

public class BasicOrderStatusDao extends AbstractDao implements IOrderStatusDao {
    public BasicOrderStatusDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
