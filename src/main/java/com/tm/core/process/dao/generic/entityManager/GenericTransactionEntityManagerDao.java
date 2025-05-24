package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.generic.IGenericTransactionDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericTransactionEntityManagerDao extends AbstractGenericTransactionEntityManagerDao implements IGenericTransactionDao {

    public GenericTransactionEntityManagerDao(EntityManager entityManager, String entityPackage) {
        super(entityManager, entityPackage);
    }

    @Override
    public <E> void persistEntity(E entity) {
        transactionHandler.persistEntity(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        transactionHandler.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        transactionHandler.deleteEntity(entity);
    }

    @Override
    public <E> void findEntityAndUpdate(Class<E> clazz, E entity, Parameter... parameters) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            E oldEntity = queryService.getEntityByDefaultNamedQuery(entityManager, clazz, parameters);
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
    public <E> void findEntityAndDelete(Class<E> clazz, Parameter... parameters) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            E entity = queryService.getEntityByDefaultNamedQuery(entityManager, clazz, parameters);
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
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graphName, Parameter... parameters) {
        return queryService.getGraphEntityList(entityManager, clazz, graphName, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graphName, Parameter... parameters) {
        return queryService.getGraphEntity(entityManager, clazz, graphName, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
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

}
