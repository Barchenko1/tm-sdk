package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.org.IOrgUserDao;
import org.hibernate.SessionFactory;

public class BasicOrgUserDao extends AbstractDao implements IOrgUserDao {
    public BasicOrgUserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
