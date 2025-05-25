package com.tm.core.process.dao.common.entityManager;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.ITransactionEntityDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.process.dao.transaction.EntityManagerTransactionHandler;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTransactionEntityManagerDao implements ITransactionEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionEntityManagerDao.class);

    protected final Class<?> clazz;
    protected final EntityManager entityManager;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;
    protected final ITransactionHandler transactionHandler;

    public AbstractTransactionEntityManagerDao(EntityManager entityManager,
                                               IQueryService queryService,
                                               Class<?> clazz) {
        this.clazz = clazz;
        this.entityManager = entityManager;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = queryService;
        this.transactionHandler = new EntityManagerTransactionHandler(entityManager);
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
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            E oldEntity = (E) queryService.getEntityByDefaultNamedQuery(entityManager, clazz, parameters);
            entityFieldHelper.setId(entity, entityFieldHelper.findId(oldEntity));
            entityManager.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
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
        try {
            transaction.begin();
            E entity = (E) queryService.getEntityByDefaultNamedQuery(entityManager, this.clazz, parameters);
            classTypeChecker(entity);
            entityManager.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
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
    @SuppressWarnings("unchecked")
    public <E> List<E> getGraphEntityList(String graphName, Parameter... parameters) {
        try {
            return (List<E>) queryService.getGraphEntityList(entityManager, clazz, graphName, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        try {
            return (List<E>) queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getGraphEntity(String graphName, Parameter... parameters) {
        try {
            return (E) queryService.getGraphEntity(entityManager, clazz, graphName, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters) {
        try {
            return (E) queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameters) {
        try {
            return (Optional<E>) queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters) {
        try {
            return (Optional<E>) queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
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

    @Override
    public Class<?> getEntityClass() {
        return this.clazz;
    }

}
