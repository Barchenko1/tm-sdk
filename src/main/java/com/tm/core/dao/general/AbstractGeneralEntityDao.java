package com.tm.core.dao.general;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.transaction.ITransactionWrapper;
import com.tm.core.dao.transaction.TransactionWrapper;
import com.tm.core.modal.GeneralEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractGeneralEntityDao implements IGeneralEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeneralEntityDao.class);

    private final Class<?> clazz;
    protected final SessionFactory sessionFactory;
    protected final IThreadLocalSessionManager sessionManager;
    private final Executor executor;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IEntityIdentifierDao entityIdentifierDao;
    protected final ITransactionWrapper transactionWrapper;

    public AbstractGeneralEntityDao(SessionFactory sessionFactory,
                                    IEntityIdentifierDao entityIdentifierDao,
                                    Class<?> clazz) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
        this.executor = Executors.newFixedThreadPool(4);
        this.entityFieldHelper = new EntityFieldHelper();
        this.entityIdentifierDao = entityIdentifierDao;
        this.transactionWrapper = new TransactionWrapper(sessionFactory);
    }

    @Override
    public void saveGeneralEntity(GeneralEntity generalEntity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<Integer> keyList = new ArrayList<>(generalEntity.getKeys());
            for (int key : keyList) {
                List<Object> objectList = generalEntity.getValues(key);
                objectList.forEach(session::persist);
            }
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
    public void saveGeneralEntity(Consumer<Session> consumer) {
        this.transactionWrapper.saveEntity(consumer);
    }

    @Override
    public <E> void updateGeneralEntity(Supplier<E> supplier) {
        this.transactionWrapper.updateEntity(supplier);
    }

    @Override
    public void updateGeneralEntity(Consumer<Session> consumer) {
        this.transactionWrapper.updateEntity(consumer);
    }

    @Override
    public <E> void deleteGeneralEntity(Class<?> clazz, Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<E> entityList = entityIdentifierDao.getEntityList(clazz, parameters);
            entityList.forEach(session::remove);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.warn("transaction error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> void deleteGeneralEntity(Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<E> entityList = entityIdentifierDao.getEntityList(this.clazz, parameters);
            entityList.forEach(session::remove);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.warn("transaction error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> List<E> getGeneralEntityList(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntityList(clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> E getGeneralEntity(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntity(clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> Optional<E> getOptionalGeneralEntity(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getOptionalEntity(clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> List<E> getGeneralEntityList(Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntityList(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> E getGeneralEntity(Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntity(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> Optional<E> getOptionalGeneralEntity(Parameter... parameters) {
        try {
            return entityIdentifierDao.getOptionalEntity(this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
