package com.tm.core.dao.single;

import com.tm.core.dao.AbstractEntityDao;
import com.tm.core.processor.ThreadLocalSessionManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSingleEntityDao extends AbstractEntityDao implements ISingleEntityDao {
    private static final Logger log = LoggerFactory.getLogger(AbstractSingleEntityDao.class);
    protected final SessionFactory sessionFactory;
    protected final ThreadLocalSessionManager sessionManager;

    public AbstractSingleEntityDao(SessionFactory sessionFactory,
                                   Class<?> clazz) {
        super(clazz);
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
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
    public <E> void updateEntity(E newEntity) {
        classTypeChecker(newEntity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(newEntity);
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
    public <E> void deleteEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
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
    @SuppressWarnings("unchecked")
    public void mutateEntityBySQLQueryWithParams(String sqlQuery, Object... params) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query query = session.createNativeQuery(sqlQuery, Void.class);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            query.executeUpdate();
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
    @SuppressWarnings("unchecked")
    public <E> List<E> getEntityListBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            return (List<E>) session
                    .createNativeQuery(sqlQuery, clazz)
                    .list();
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getEntityListBySQLQueryWithParams(String sqlQuery, Object... params) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> query = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.list();
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getEntityBySQLQueryWithParams(String sqlQuery, Object... params) {
        try {
            Session session = sessionManager.getSession();
            NativeQuery<E> query = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.getSingleResult();
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getOptionalEntityBySQLQueryWithParams(String sqlQuery, Object... params) {
        try {
            Session session = sessionManager.getSession();
            NativeQuery<E> query = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.uniqueResultOptional();
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
