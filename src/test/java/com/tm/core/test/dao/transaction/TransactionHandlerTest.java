package com.tm.core.test.dao.transaction;

import com.tm.core.process.dao.transaction.SessionTransactionHandler;
import com.tm.core.modal.relationship.Item;
import com.tm.core.test.dao.AbstractDaoTest;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionHandlerTest extends AbstractDaoTest {

    private static SessionTransactionHandler transactionHandler;

    @BeforeAll
    public static void setupAll() {
        transactionHandler = new SessionTransactionHandler(sessionFactory);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionFactoryField = SessionTransactionHandler.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionHandler, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void persistSupplier_success() {
        loadDataSet("/datasets/single/emptyItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setName("New Entity");

        Supplier<Item> ItemSupplier = () -> Item;

        transactionHandler.persistSupplier(ItemSupplier);
        verifyExpectedData("/datasets/single/saveItemEntityDataSet.yml");
    }

    @Test
    void persistSupplier_transactionFailure() {
        Item Item = new Item();
        Item.setName("New Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = SessionTransactionHandler.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionHandler, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(Item);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Supplier<Item> ItemSupplier = () -> Item;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionHandler.persistSupplier(ItemSupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void persistSupplierConsumer_success() {
        loadDataSet("/datasets/single/emptyItemEntityDataSet.yml");
        Consumer<EntityManager> sessionConsumer = (EntityManager em) -> {
            Item Item = new Item();
            Item.setName("New Entity");
            em.persist(Item);
        };

        transactionHandler.executeConsumer(sessionConsumer);
        verifyExpectedData("/datasets/single/saveItemEntityDataSet.yml");
    }

    @Test
    void persistSupplierConsumer_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = SessionTransactionHandler.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionHandler, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(any(Item.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Consumer<EntityManager> sessionConsumer = (EntityManager em) -> {
            em.persist(new Item());
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionHandler.executeConsumer(sessionConsumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void updateSupplier_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setId(1);
        Item.setName("Update Entity");
        Supplier<Item> ItemSupplier = () -> Item;

        transactionHandler.mergeSupplier(ItemSupplier);
        verifyExpectedData("/datasets/single/updateItemEntityDataSet.yml");
    }

    @Test
    void updateSupplier_transactionFailure() {
        Item Item = new Item();
        Item.setId(100L);
        Item.setName("Update Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = SessionTransactionHandler.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionHandler, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Supplier<Item> ItemSupplier = () -> Item;

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(Item);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionHandler.mergeSupplier(ItemSupplier));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void updateSupplierConsumer_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Consumer<EntityManager> sessionConsumer = (EntityManager em) -> {
            Item Item = new Item();
            Item.setId(1);
            Item.setName("Update Entity");
            em.merge(Item);
        };
        transactionHandler.executeConsumer(sessionConsumer);
        verifyExpectedData("/datasets/single/updateItemEntityDataSet.yml");
    }

    @Test
    void updateSupplierConsumer_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = SessionTransactionHandler.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionHandler, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Consumer<EntityManager> sessionConsumer = (EntityManager em) -> {
            Item Item = new Item();
            Item.setId(1L);
            Item.setName("Update Entity");
            em.merge(Item);
            throw new RuntimeException();
        };

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(any(Item.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionHandler.executeConsumer(sessionConsumer));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void deleteSupplier_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setId(1);
        Supplier<Item> ItemSupplier = () -> Item;

        transactionHandler.deleteSupplier(ItemSupplier);
        verifyExpectedData("/datasets/single/emptyItemEntityDataSet.yml");
    }

    @Test
    void deleteSupplier_transactionFailure() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setId(100);
        Supplier<Item> ItemSupplier = () -> Item;

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = SessionTransactionHandler.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionHandler, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(Item);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionHandler.deleteSupplier(ItemSupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();

        verifyExpectedData("/datasets/single/testItemEntityDataSet.yml");
    }

}
