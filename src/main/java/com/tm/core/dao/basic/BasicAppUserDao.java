package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.user.IAppUserDao;
import org.hibernate.SessionFactory;

public class BasicAppUserDao extends AbstractDao implements IAppUserDao {
    public BasicAppUserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
