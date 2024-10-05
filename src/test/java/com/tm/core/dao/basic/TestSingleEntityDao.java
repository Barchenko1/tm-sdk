package com.tm.core.dao.basic;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.single.AbstractSingleEntityDao;
import com.tm.core.modal.single.SingleTestEntity;
import org.hibernate.SessionFactory;

public class TestSingleEntityDao extends AbstractSingleEntityDao {

    public TestSingleEntityDao(SessionFactory sessionFactory, IEntityIdentifierDao entityIdentifierDao) {
        super(sessionFactory, entityIdentifierDao, SingleTestEntity.class);
    }

}
