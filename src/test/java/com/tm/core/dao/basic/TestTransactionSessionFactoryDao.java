package com.tm.core.dao.basic;

import com.tm.core.process.dao.common.session.AbstractTransactionSessionFactoryDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.modal.relationship.Employee;
import org.hibernate.SessionFactory;

public class TestTransactionSessionFactoryDao extends AbstractTransactionSessionFactoryDao {
    public TestTransactionSessionFactoryDao(SessionFactory sessionFactory, IQueryService queryService) {
        super(sessionFactory, queryService, Employee.class);
    }
}
