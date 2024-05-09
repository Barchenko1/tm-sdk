package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.payment.ICardDao;
import org.hibernate.SessionFactory;

public class BasicCardDao extends AbstractDao implements ICardDao {
    public BasicCardDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
