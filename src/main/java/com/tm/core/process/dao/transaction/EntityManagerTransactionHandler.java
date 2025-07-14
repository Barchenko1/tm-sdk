package com.tm.core.process.dao.transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityManagerTransactionHandler implements ITransactionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerTransactionHandler.class);

    private final EntityManager entityManager;

    public EntityManagerTransactionHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            consumer.accept(entityManager);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <T> T executeFunction(Function<EntityManager, T> function) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            T result = function.apply(entityManager);
            if (isNewTransaction) {
                transaction.commit();
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> void persistEntity(E entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            entityManager.persist(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> void mergeEntity(E entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            entityManager.merge(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> void deleteEntity(E entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            entityManager.remove(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
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
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E entity = supplier.get();
            entityManager.persist(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E entity = supplier.get();
            entityManager.merge(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            E entity = supplier.get();
            entityManager.remove(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            LOGGER.error("transaction error", e);
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            entityManager.clear();
        }
    }

}
