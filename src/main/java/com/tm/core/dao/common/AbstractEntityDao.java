package com.tm.core.dao.common;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.transaction.ITransactionWrapper;
import com.tm.core.dao.transaction.TransactionWrapper;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AbstractEntityDao implements IEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityDao.class);

    protected final Class<?> clazz;
    protected final SessionFactory sessionFactory;
    protected final IThreadLocalSessionManager sessionManager;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IEntityIdentifierDao entityIdentifierDao;
    protected final ITransactionWrapper transactionWrapper;

    public AbstractEntityDao(SessionFactory sessionFactory,
                             IEntityIdentifierDao entityIdentifierDao,
                             Class<?> clazz) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
        this.entityFieldHelper = new EntityFieldHelper();
        this.entityIdentifierDao = entityIdentifierDao;
        this.transactionWrapper = new TransactionWrapper(sessionFactory);
    }

    @Override
    public <E> void persistEntity(E entity) {
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
    public <E> void mergeEntity(E entity) {
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
            classTypeChecker(entity);
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
    public <E> void saveEntity(Supplier<E> supplier) {
        transactionWrapper.saveEntity(supplier);
    }

    @Override
    public <E> void updateEntity(Supplier<E> supplier) {
        transactionWrapper.updateEntity(supplier);
    }

    @Override
    public <E> void deleteEntity(Supplier<E> supplier) {
        transactionWrapper.deleteEntity(supplier);
    }

    @Override
    public void saveEntity(Consumer<Session> consumer) {
        transactionWrapper.saveEntity(consumer);
    }

    @Override
    public void updateEntity(Consumer<Session> consumer) {
        transactionWrapper.updateEntity(consumer);
    }

    @Override
    public void deleteEntity(Consumer<Session> consumer) {
        transactionWrapper.deleteEntity(consumer);
    }

    @Override
    public <E> List<E> getEntityList(Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntityList(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
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
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Parameter... parameters) {
        try {
            return entityIdentifierDao.getOptionalEntity(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    private <E> void classTypeChecker(E entity) {
        if (this.clazz != entity.getClass()) {
            LOGGER.warn("Invalid entity type {} != {}", this.clazz, entity.getClass());
            throw new RuntimeException(
                    String.format("Invalid entity type %s != %s", this.clazz, entity.getClass())
            );
        }
    }
}
