package com.tm.core.test.dao.transaction;

import com.tm.core.modal.relationship.Item;
import com.tm.core.process.dao.transaction.EntityManagerTransactionHandler;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.test.dao.AbstractDaoTest;
import jakarta.persistence.EntityManager;
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

public class EntityManagerTransactionHandlerTest extends AbstractDaoTest {

    private static ITransactionHandler transactionHandler;

    @BeforeAll
    public static void setupAll() {
        transactionHandler = new EntityManagerTransactionHandler(entityManager);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field entityManagerField = EntityManagerTransactionHandler.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(transactionHandler, entityManager);
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

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field entityManagerField = EntityManagerTransactionHandler.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(transactionHandler, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).persist(Item);
        when(transaction.isActive()).thenReturn(false);
        doNothing().when(transaction).rollback();

        Supplier<Item> ItemSupplier = () -> Item;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionHandler.persistSupplier(ItemSupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(entityManager).clear();
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
        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field entityManagerField = EntityManagerTransactionHandler.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(transactionHandler, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).persist(any(Item.class));
        when(transaction.isActive()).thenReturn(false);
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
        verify(entityManager).clear();
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

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field entityManagerField = EntityManagerTransactionHandler.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(transactionHandler, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Supplier<Item> ItemSupplier = () -> Item;

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).merge(Item);
        when(transaction.isActive()).thenReturn(false);
        doNothing().when(transaction).rollback();

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionHandler.mergeSupplier(ItemSupplier));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(entityManager).clear();
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
        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field entityManagerField = EntityManagerTransactionHandler.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(transactionHandler, entityManager);
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

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).merge(any(Item.class));
        when(transaction.isActive()).thenReturn(false);
        doNothing().when(transaction).rollback();

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionHandler.executeConsumer(sessionConsumer));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(entityManager).clear();
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

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field entityManagerField = EntityManagerTransactionHandler.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(transactionHandler, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).remove(Item);
        when(transaction.isActive()).thenReturn(false);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionHandler.deleteSupplier(ItemSupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(entityManager).clear();

        verifyExpectedData("/datasets/single/testItemEntityDataSet.yml");
    }

}
