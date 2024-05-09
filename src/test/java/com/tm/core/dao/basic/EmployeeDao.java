package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeDao extends AbstractDao implements IEmployeeDao {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeDao.class);

    public EmployeeDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
