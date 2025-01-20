package com.tm.core.dao.basic;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.transitive.AbstractTransitiveSelfEntityDao;
import com.tm.core.modal.transitive.TransitiveSelfEntity;
import org.hibernate.SessionFactory;

public class TestTransitiveSelfEntityDao extends AbstractTransitiveSelfEntityDao {

    public TestTransitiveSelfEntityDao(SessionFactory sessionFactory,
                                       IEntityIdentifierDao entityIdentifierDao) {
        super(sessionFactory, entityIdentifierDao, TransitiveSelfEntity.class);
    }

}
