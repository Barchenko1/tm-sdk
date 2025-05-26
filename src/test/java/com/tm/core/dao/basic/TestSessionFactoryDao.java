package com.tm.core.dao.basic;

import com.tm.core.modal.relationship.Employee;
import com.tm.core.process.dao.common.session.AbstractSessionFactoryDao;
import com.tm.core.process.dao.query.IQueryService;
import org.hibernate.SessionFactory;

public class TestSessionFactoryDao extends AbstractSessionFactoryDao {
    public TestSessionFactoryDao(SessionFactory sessionFactory, IQueryService queryService) {
        super(sessionFactory, queryService, Employee.class);
    }
}
