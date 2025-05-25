package com.tm.core.dao.basic;

import com.tm.core.modal.relationship.Employee;
import com.tm.core.process.dao.common.entityManager.AbstractTransactionEntityManagerDao;
import com.tm.core.process.dao.query.IQueryService;
import jakarta.persistence.EntityManager;

public class TestTransactionEntityManagerDao extends AbstractTransactionEntityManagerDao {
    public TestTransactionEntityManagerDao(EntityManager entityManager, IQueryService queryService) {
        super(entityManager, queryService, Employee.class);
    }
}
