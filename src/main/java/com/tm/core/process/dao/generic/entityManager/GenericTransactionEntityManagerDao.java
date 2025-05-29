package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericTransactionEntityManagerDao extends AbstractGenericTransactionEntityManagerDao {

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
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        try {
            return queryService.getGraphEntityList(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try {
            return queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        try {
            return queryService.getGraphEntity(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try {
            return queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        try {
            return queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        return List.of();
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return null;
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return Optional.empty();
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try {
            return queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return List.of();
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return null;
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return Optional.empty();
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
