package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.user.IUserDetailDao;
import org.hibernate.SessionFactory;

public class BasicUserDetailDao extends AbstractDao implements IUserDetailDao {
    public BasicUserDetailDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
