package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.business.IBusinessAddressDao;
import org.hibernate.SessionFactory;

public class BasicBusinessAddressDao extends AbstractDao implements IBusinessAddressDao {
    public BasicBusinessAddressDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
