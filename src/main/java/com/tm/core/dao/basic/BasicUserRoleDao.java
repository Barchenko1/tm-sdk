package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.user.IUserRoleDao;
import org.hibernate.SessionFactory;

public class BasicUserRoleDao extends AbstractDao implements IUserRoleDao {
    public BasicUserRoleDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
