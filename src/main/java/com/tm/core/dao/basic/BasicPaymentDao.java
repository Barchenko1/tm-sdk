package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.payment.IPaymentDao;
import org.hibernate.SessionFactory;

public class BasicPaymentDao extends AbstractDao implements IPaymentDao {
    public BasicPaymentDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
