package com.tm.core.dao.basic;

import com.tm.core.process.dao.common.AbstractEntityDao;
import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.modal.relationship.Employee;
import org.hibernate.SessionFactory;

public class TestEntityDao extends AbstractEntityDao {
    public TestEntityDao(SessionFactory sessionFactory,
                         IQueryService queryService) {
        super(sessionFactory, queryService, Employee.class);
    }
}
