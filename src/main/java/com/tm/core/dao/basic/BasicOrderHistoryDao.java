package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.order.IOrderHistoryDao;
import org.hibernate.SessionFactory;

public class BasicOrderHistoryDao extends AbstractDao implements IOrderHistoryDao {
    public BasicOrderHistoryDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
