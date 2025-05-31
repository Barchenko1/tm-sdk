package com.tm.core.process.dao.common.session;

import com.tm.core.process.dao.common.ITransactionEntityDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.dao.transaction.SessionTransactionHandler;
import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTransactionSessionFactoryDao extends AbstractSessionFactoryDao implements ITransactionEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionSessionFactoryDao.class);

    protected final ITransactionHandler transactionHandler;

    public AbstractTransactionSessionFactoryDao(SessionFactory sessionFactory,
                                                IQueryService queryService,
                                                Class<?> clazz) {
        super(sessionFactory, queryService, clazz);
        this.transactionHandler = new SessionTransactionHandler(sessionFactory);
    }

    @Override
    public <E> void persistEntity(E entity) {
        classTypeChecker(entity);
        transactionHandler.persistEntity(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        classTypeChecker(entity);
        transactionHandler.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        classTypeChecker(entity);
        transactionHandler.deleteEntity(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void findEntityAndUpdate(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E oldEntity = (E) queryService.getEntityByDefaultNamedQuery(session, this.clazz, parameters);
            entityFieldHelper.setId(entity, entityFieldHelper.findId(oldEntity));
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void findEntityAndDelete(Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = (E) queryService.getEntityByDefaultNamedQuery(session, this.clazz, parameters);
            classTypeChecker(entity);
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        transactionHandler.persistSupplier(supplier);

    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        transactionHandler.mergeSupplier(supplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        transactionHandler.deleteSupplier(supplier);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        transactionHandler.executeConsumer(consumer);
    }

    @Override
    public Class<?> getEntityClass() {
        return this.clazz;
    }

}
