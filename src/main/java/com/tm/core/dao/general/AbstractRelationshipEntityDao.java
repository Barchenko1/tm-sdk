package com.tm.core.dao.general;

import com.tm.core.dao.AbstractEntityDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRelationshipEntityDao extends AbstractEntityDao implements IRelationshipEntityDao {

    private static final Logger log = LoggerFactory.getLogger(AbstractRelationshipEntityDao.class);
    protected final SessionFactory sessionFactory;

    public AbstractRelationshipEntityDao(SessionFactory sessionFactory, Class<?> clazz) {
        super(clazz);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <E> void saveEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            log.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void updateEntity(E entity) {

    }

    @Override
    public <E> void deleteEntity(E entity) {

    }
}
