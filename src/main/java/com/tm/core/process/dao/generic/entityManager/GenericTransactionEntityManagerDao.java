package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Consumer;
import java.util.function.Function;
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
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E oldEntity = queryService.getEntityByDefaultNamedQuery(entityManager, clazz, parameters);
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
    public <E> void findEntityAndDelete(Class<E> clazz, Parameter... parameters) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E entity = queryService.getEntityByDefaultNamedQuery(entityManager, clazz, parameters);
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
