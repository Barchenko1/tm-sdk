package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.transaction.TransactionWrapper;
import com.tm.core.modal.single.SingleTestEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
public class TransactionWrapperTest {

    private static SessionFactory sessionFactory;
    private static TransactionWrapper transactionWrapper;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        sessionFactory = getSessionFactory();
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
    @DataSet("datasets/single/emptySingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/saveSingleEntityDataSet.yml")
    void saveEntity_success() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setName("New Entity");

        Supplier<SingleTestEntity> singleTestEntitySupplier = () -> singleTestEntity;

        transactionWrapper.saveEntity(singleTestEntitySupplier);
    }

    @Test
    void saveEntity_transactionFailure() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setName("New Entity");

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
        doThrow(new RuntimeException()).when(session).persist(singleTestEntity);

        Supplier<SingleTestEntity> singleTestEntitySupplier = () -> singleTestEntity;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionWrapper.saveEntity(singleTestEntitySupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    @DataSet("datasets/single/emptySingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/saveSingleEntityDataSet.yml")
    void saveEntityConsumer_success() {
        Consumer<Session> sessionConsumer = (Session s) -> {
            SingleTestEntity singleTestEntity = new SingleTestEntity();
            singleTestEntity.setName("New Entity");
            s.persist(singleTestEntity);
        };

        transactionWrapper.saveEntity(sessionConsumer);
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
        doThrow(new RuntimeException()).when(session).persist(any(SingleTestEntity.class));

        Consumer<Session> sessionConsumer = (Session s) -> {
            s.persist(new SingleTestEntity());
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionWrapper.saveEntity(sessionConsumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/updateSingleEntityDataSet.yml")
    void updateEntity_success() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(1);
        singleTestEntity.setName("Update Entity");
        Supplier<SingleTestEntity> singleTestEntitySupplier = () -> singleTestEntity;

        transactionWrapper.updateEntity(singleTestEntitySupplier);
    }

    @Test
    void updateEntity_transactionFailure() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(100L);
        singleTestEntity.setName("Update Entity");

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

        Supplier<SingleTestEntity> singleTestEntitySupplier = () -> singleTestEntity;

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(singleTestEntity);

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionWrapper.updateEntity(singleTestEntitySupplier));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/updateSingleEntityDataSet.yml")
    void updateEntityConsumer_success() {
        Consumer<Session> sessionConsumer = (Session s) -> {
            SingleTestEntity singleTestEntity = new SingleTestEntity();
            singleTestEntity.setId(1);
            singleTestEntity.setName("Update Entity");
            s.merge(singleTestEntity);
        };
        transactionWrapper.updateEntity(sessionConsumer);
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
            SingleTestEntity singleTestEntity = new SingleTestEntity();
            singleTestEntity.setId(1L);
            singleTestEntity.setName("Update Entity");
            s.merge(singleTestEntity);
            throw new RuntimeException();
        };

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(any(SingleTestEntity.class));

        Exception exception =
                assertThrows(RuntimeException.class, () -> transactionWrapper.updateEntity(sessionConsumer));

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/emptySingleEntityDataSet.yml")
    void deleteEntity_success() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(1);
        Supplier<SingleTestEntity> singleTestEntitySupplier = () -> singleTestEntity;

        transactionWrapper.deleteEntity(singleTestEntitySupplier);
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/testSingleEntityDataSet.yml")
    void deleteEntity_transactionFailure() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(100);
        Supplier<SingleTestEntity> singleTestEntitySupplier = () -> singleTestEntity;

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
        doThrow(new RuntimeException()).when(session).remove(singleTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionWrapper.deleteEntity(singleTestEntitySupplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }
    
}
