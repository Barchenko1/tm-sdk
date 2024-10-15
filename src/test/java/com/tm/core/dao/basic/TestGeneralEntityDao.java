package com.tm.core.dao.basic;

import com.tm.core.dao.general.AbstractGeneralEntityDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.relationship.RelationshipRootTestEntity;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

import java.util.function.Supplier;

public class TestGeneralEntityDao extends AbstractGeneralEntityDao {

    public TestGeneralEntityDao(SessionFactory sessionFactory, IEntityIdentifierDao entityIdentifierDao) {
        super(sessionFactory, entityIdentifierDao, RelationshipRootTestEntity.class);
    }

}
