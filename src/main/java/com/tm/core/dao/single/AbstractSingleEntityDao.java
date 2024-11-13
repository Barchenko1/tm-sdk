package com.tm.core.dao.single;

import com.tm.core.dao.AbstractEntityDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSingleEntityDao extends AbstractEntityDao implements ISingleEntityDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSingleEntityDao.class);
    protected final SessionFactory sessionFactory;
    protected final IThreadLocalSessionManager sessionManager;
    protected final IEntityIdentifierDao entityIdentifierDao;
    protected final IEntityFieldHelper entityFieldHelper;

    public AbstractSingleEntityDao(SessionFactory sessionFactory,
                                   IEntityIdentifierDao entityIdentifierDao,
                                   Class<?> clazz) {
        super(clazz);
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
        this.entityFieldHelper = new EntityFieldHelper();
        this.entityIdentifierDao = entityIdentifierDao;
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
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }


    @Override
    public <E> void updateEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
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
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void findEntityAndUpdate(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try {
            Session session = sessionManager.getSession();
            transaction = session.beginTransaction();
            E oldEntity = entityIdentifierDao.getEntity(this.clazz, parameters);
            entityFieldHelper.setId(entity, entityFieldHelper.findId(oldEntity));
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> void findEntityAndDelete(Parameter... parameters) {
        Transaction transaction = null;
        try {
            Session session = sessionManager.getSession();
            transaction = session.beginTransaction();
            E entity = entityIdentifierDao.getEntity(this.clazz, parameters);
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> List<E> getEntityList(Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntityList(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> E getEntity(Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntity(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Parameter... parameters) {
        try {
            return entityIdentifierDao.getOptionalEntity(this.clazz, parameters);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("get optional entity error {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> List<E> getEntityList(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntityList(clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> E getEntity(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntity(clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getOptionalEntity(clazz, parameters);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            sessionManager.closeSession();
        }
    }

}
