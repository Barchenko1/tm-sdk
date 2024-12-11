package com.tm.core.dao.basic;

import com.tm.core.dao.common.AbstractEntityDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.relationship.RelationshipRootTestEntity;
import org.hibernate.SessionFactory;

public class TestEntityDao extends AbstractEntityDao {
    public TestEntityDao(SessionFactory sessionFactory,
                         IEntityIdentifierDao entityIdentifierDao) {
        super(sessionFactory, entityIdentifierDao, RelationshipRootTestEntity.class);
    }
}
