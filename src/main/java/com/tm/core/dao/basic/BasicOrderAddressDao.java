package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.order.IOrderAddressDao;
import org.hibernate.SessionFactory;

public class BasicOrderAddressDao extends AbstractDao implements IOrderAddressDao {
    public BasicOrderAddressDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
