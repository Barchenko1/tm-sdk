package com.tm.core.process.dao.common.entityManager;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.ITransactionEntityDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.process.dao.transaction.EntityManagerTransactionHandler;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractTransactionEntityManagerDao extends AbstractEntityManagerDao implements ITransactionEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionEntityManagerDao.class);

    protected final ITransactionHandler transactionHandler;

    public AbstractTransactionEntityManagerDao(EntityManager entityManager,
                                               IQueryService queryService,
                                               Class<?> clazz) {
        super(entityManager, queryService, clazz);
        this.transactionHandler = new EntityManagerTransactionHandler(entityManager);
    }

    @Override
    public <E> void persistEntity(E entity) {
        LOGGER.trace("persist Entity, {}", entity);
        classTypeChecker(entity);
        transactionHandler.persistEntity(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        LOGGER.info("merging entity: {}", entity);
        classTypeChecker(entity);
        transactionHandler.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("delete entity: {}", entity);
        classTypeChecker(entity);
        transactionHandler.deleteEntity(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void findEntityAndUpdate(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E oldEntity = (E) queryService.getEntityByDefaultNamedQuery(entityManager, clazz, parameters);
            entityFieldHelper.setId(entity, entityFieldHelper.findId(oldEntity));
            entityManager.merge(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void findEntityAndDelete(Parameter... parameters) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E entity = (E) queryService.getEntityByDefaultNamedQuery(entityManager, this.clazz, parameters);
            classTypeChecker(entity);
            entityManager.remove(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
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
    public <T> T executeFunction(Function<EntityManager, T> function) {
        return transactionHandler.executeFunction(function);
    }
}
