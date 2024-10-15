package com.tm.core.dao.transaction;

import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TransactionWrapper implements ITransactionWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionWrapper.class);

    private final SessionFactory sessionFactory;
    private final IThreadLocalSessionManager sessionManager;

    public TransactionWrapper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(this.sessionFactory);
    }

    @Override
    public <E> void saveEntity(Supplier<E> supplier) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = supplier.get();
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
    public void saveEntity(Consumer<Session> consumer) {
        transactionWrap(consumer);
    }

    @Override
    public <E> void updateEntity(Supplier<E> supplier) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = supplier.get();
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
    public void updateEntity(Consumer<Session> consumer) {
        transactionWrap(consumer);
    }

    @Override
    public <E> void deleteEntity(Supplier<E> supplier) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = supplier.get();
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
    public void deleteEntity(Consumer<Session> consumer) {
        transactionWrap(consumer);
    }

    private void transactionWrap(Consumer<Session> consumer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            consumer.accept(session);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
