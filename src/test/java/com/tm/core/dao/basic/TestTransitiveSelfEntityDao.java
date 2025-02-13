package com.tm.core.dao.basic;

import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.process.dao.transitive.AbstractTransitiveSelfEntityDao;
import com.tm.core.modal.transitive.TransitiveSelfEntity;
import org.hibernate.SessionFactory;

public class TestTransitiveSelfEntityDao extends AbstractTransitiveSelfEntityDao {

    public TestTransitiveSelfEntityDao(SessionFactory sessionFactory,
                                       IQueryService queryService) {
        super(sessionFactory, queryService, TransitiveSelfEntity.class);
    }

}
