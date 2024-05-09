package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.org.IOrganizationDao;
import org.hibernate.SessionFactory;

public class BasicOrganizationDao extends AbstractDao implements IOrganizationDao {
    public BasicOrganizationDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}
