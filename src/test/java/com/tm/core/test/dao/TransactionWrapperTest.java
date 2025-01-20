package com.tm.core.test.dao;

import com.tm.core.dao.transaction.TransactionWrapper;
import com.tm.core.modal.relationship.Item;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionWrapperTest extends AbstractDaoTest {

    private static TransactionWrapper transactionWrapper;

    @BeforeAll
    public static void setupAll() {
        transactionWrapper = new TransactionWrapper(sessionFactory);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionFactoryField = TransactionWrapper.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionWrapper, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveEntity_success() {
        loadDataSet("/datasets/single/emptyItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setName("New Entity");

        Supplier<Item> ItemSupplier = () -> Item;

        transactionWrapper.saveEntity(ItemSupplier);
        verifyExpectedData("/datasets/single/saveItemEntityDataSet.yml");
    }

    @Test
    void saveEntity_transactionFailure() {
        Item Item = new Item();
        Item.setName("New Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = TransactionWrapper.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionWrapper, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(Item);

        Supplier<Item> ItemSupplier = () -> Item;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionWrapper.saveEntity(ItemSupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void saveEntityConsumer_success() {
        loadDataSet("/datasets/single/emptyItemEntityDataSet.yml");
        Consumer<Session> sessionConsumer = (Session s) -> {
            Item Item = new Item();
            Item.setName("New Entity");
            s.persist(Item);
        };

        transactionWrapper.executeConsumer(sessionConsumer);
        verifyExpectedData("/datasets/single/saveItemEntityDataSet.yml");
    }

    @Test
    void saveEntityConsumer_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = TransactionWrapper.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionWrapper, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(any(Item.class));

        Consumer<Session> sessionConsumer = (Session s) -> {
            s.persist(new Item());
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionWrapper.executeConsumer(sessionConsumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void updateEntity_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setId(1);
        Item.setName("Update Entity");
        Supplier<Item> ItemSupplier = () -> Item;

        transactionWrapper.updateEntity(ItemSupplier);
        verifyExpectedData("/datasets/single/updateItemEntityDataSet.yml");
    }

    @Test
    void updateEntity_transactionFailure() {
        Item Item = new Item();
        Item.setId(100L);
        Item.setName("Update Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = TransactionWrapper.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionWrapper, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Supplier<Item> ItemSupplier = () -> Item;

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(Item);

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionWrapper.updateEntity(ItemSupplier));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void updateEntityConsumer_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Consumer<Session> sessionConsumer = (Session s) -> {
            Item Item = new Item();
            Item.setId(1);
            Item.setName("Update Entity");
            s.merge(Item);
        };
        transactionWrapper.executeConsumer(sessionConsumer);
        verifyExpectedData("/datasets/single/updateItemEntityDataSet.yml");
    }

    @Test
    void updateEntityConsumer_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = TransactionWrapper.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionWrapper, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Consumer<Session> sessionConsumer = (Session s) -> {
            Item Item = new Item();
            Item.setId(1L);
            Item.setName("Update Entity");
            s.merge(Item);
            throw new RuntimeException();
        };

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(any(Item.class));

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionWrapper.executeConsumer(sessionConsumer));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void deleteEntity_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setId(1);
        Supplier<Item> ItemSupplier = () -> Item;

        transactionWrapper.deleteEntity(ItemSupplier);
        verifyExpectedData("/datasets/single/emptyItemEntityDataSet.yml");
    }

    @Test
    void deleteEntity_transactionFailure() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Item Item = new Item();
        Item.setId(100);
        Supplier<Item> ItemSupplier = () -> Item;

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = TransactionWrapper.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(transactionWrapper, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(Item);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionWrapper.deleteEntity(ItemSupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();

        verifyExpectedData("/datasets/single/testItemEntityDataSet.yml");
    }
    
}
