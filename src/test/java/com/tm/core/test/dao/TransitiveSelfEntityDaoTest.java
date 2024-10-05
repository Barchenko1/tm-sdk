package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.dsl.RiderDSL;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.basic.TestTransitiveSelfEntityDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.transitive.AbstractTransitiveSelfEntityDao;
import com.tm.core.dao.transitive.ITransitiveSelfEntityDao;
import com.tm.core.modal.transitive.TransitiveSelfTestEntity;
import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.util.TransitiveSelfEnum;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
class TransitiveSelfEntityDaoTest {

    private static SessionFactory sessionFactory;

    @Mock
    private static IEntityIdentifierDao entityIdentifierDao;

    @InjectMocks
    private static TestTransitiveSelfEntityDao testTransitiveSelfEntityDao;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;
        try {
            RiderDSL.withConnection(connectionHolder.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SessionFactory sessionFactory = getSessionFactory();
        testTransitiveSelfEntityDao = new TestTransitiveSelfEntityDao(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getTransitiveSelfEntityList_success() {
        Parameter parameter = new Parameter("id", 1L);

        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(1L);
        transitiveSelfTestEntity.setName("TransitiveSelfEntity");
        when(entityIdentifierDao.getEntityList(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(List.of(transitiveSelfTestEntity));

        List<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getTransitiveSelfEntityList(parameter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("TransitiveSelfEntity", result.get(0).getName());
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getTransitiveSelfEntityListWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(1L);
        transitiveSelfTestEntity.setName("TransitiveSelfEntity");
        when(entityIdentifierDao.getEntityList(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(List.of(transitiveSelfTestEntity));

        List<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getTransitiveSelfEntityList(TransitiveSelfTestEntity.class, parameter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("TransitiveSelfEntity", result.get(0).getName());
    }

    @Test
    void getTransitiveSelfEntityList_Failure() {
        Parameter parameter = new Parameter("id", 1L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntityList(TransitiveSelfTestEntity.class, parameter);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntityList(parameter);
        });
    }

    @Test
    void getTransitiveSelfEntityListWithClass_Failure() {
        Parameter parameter = new Parameter("id", 1L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntityList(TransitiveSelfTestEntity.class, parameter);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntityList(TransitiveSelfTestEntity.class, parameter);
        });
    }

    @Test
    @DataSet("datasets/transitive_self/emptyTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/saveTransitiveSelfEntityDataSet.yml")
    void saveEntityTree_success() {
        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setName("New TransitiveSelfEntity");

        testTransitiveSelfEntityDao.saveEntityTree(transitiveSelfTestEntity);
    }

    @Test
    void saveEntityTree_transactionFailure() {
        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setName("New TransitiveSelfEntity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        ITransitiveSelfEntityDao testTransitiveSelfEntityDao =
                new TestTransitiveSelfEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(transitiveSelfTestEntity);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.saveEntityTree(transitiveSelfTestEntity);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/updateTransitiveSelfEntityDataSet.yml")
    void updateEntityTree_success() {
        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setName("Update TransitiveSelfEntity");

        TransitiveSelfTestEntity transitiveSelfTestEntityOld = new TransitiveSelfTestEntity();
        transitiveSelfTestEntityOld.setId(1);
        transitiveSelfTestEntityOld.setName("Test TransitiveSelfEntity");

        Parameter parameter = new Parameter("id", 1L);
        when(entityIdentifierDao.getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(transitiveSelfTestEntityOld);

        testTransitiveSelfEntityDao.updateEntityTree(transitiveSelfTestEntity, parameter);
    }

    @Test
    void updateEntityTree_transactionFailure() {
        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(100L);
        transitiveSelfTestEntity.setName("Update Entity");

        Parameter parameter = new Parameter("id", 100L);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        ITransitiveSelfEntityDao testTransitiveSelfEntityDao =
                new TestTransitiveSelfEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(entityIdentifierDao.getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(transitiveSelfTestEntity);
        doThrow(new RuntimeException()).when(session).merge(transitiveSelfTestEntity);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.updateEntityTree(transitiveSelfTestEntity, parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/emptyTransitiveSelfEntityDataSet.yml")
    void deleteEntityTree_success() {
        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(1);

        Parameter parameter = new Parameter("id", 1L);

        when(entityIdentifierDao.getEntity(TransitiveSelfTestEntity.class, parameter)).thenReturn(transitiveSelfTestEntity);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
    }

    @Test
    void deleteEntityTree_transactionFailure() {
        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(100);

        Parameter parameter = new Parameter("id", 100L);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        ITransitiveSelfEntityDao testTransitiveSelfEntityDao =
                new TestTransitiveSelfEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(entityIdentifierDao.getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(transitiveSelfTestEntity);
        doThrow(new RuntimeException()).when(session).remove(transitiveSelfTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.deleteEntityTree(parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void getOptionalTransitiveSelfEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        TransitiveSelfTestEntity TransitiveSelfTestEntity = new TransitiveSelfTestEntity();
        TransitiveSelfTestEntity.setId(1L);
        TransitiveSelfTestEntity.setName("Test TransitiveSelfEntity");
        when(entityIdentifierDao.getOptionalEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(Optional.of(TransitiveSelfTestEntity));

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);

        assertTrue(result.isPresent());
        TransitiveSelfTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Test TransitiveSelfEntity", resultEntity.getName());
    }

    @Test
    void getOptionalTransitiveSelfEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        TransitiveSelfTestEntity TransitiveSelfTestEntity = new TransitiveSelfTestEntity();
        TransitiveSelfTestEntity.setId(1L);
        TransitiveSelfTestEntity.setName("Test TransitiveSelfEntity");
        when(entityIdentifierDao.getOptionalEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(Optional.of(TransitiveSelfTestEntity));

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(com.tm.core.modal.transitive.TransitiveSelfTestEntity.class, parameter);

        assertTrue(result.isPresent());
        TransitiveSelfTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Test TransitiveSelfEntity", resultEntity.getName());
    }

    @Test
    void getOptionalTransitiveSelfEntity_Failure() {
        Parameter parameter = new Parameter("id", 1L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getOptionalEntity(TransitiveSelfTestEntity.class, parameter);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);
        });
    }

    @Test
    void getOptionalTransitiveSelfEntityWithClass_Failure() {
        Parameter parameter = new Parameter("id", 1L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getOptionalEntity(TransitiveSelfTestEntity.class, parameter);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);
        });
    }

    @Test
    void getOptionalTransitiveSelfEntity_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new NoResultException()).when(entityIdentifierDao).getOptionalEntity(eq(TransitiveSelfTestEntity.class), eq(parameter));

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    void getOptionalTransitiveSelfEntityWithClass_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new NoResultException()).when(entityIdentifierDao).getOptionalEntity(eq(TransitiveSelfTestEntity.class), eq(parameter));

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransitiveSelfEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(1L);
        transitiveSelfTestEntity.setName("Test TransitiveSelfEntity");
        when(entityIdentifierDao.getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(transitiveSelfTestEntity);

        TransitiveSelfTestEntity result = testTransitiveSelfEntityDao.getTransitiveSelfEntity(parameter);

        assertEquals(1L, result.getId());
        assertEquals("Test TransitiveSelfEntity", result.getName());
    }

    @Test
    void getTransitiveSelfEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setId(1L);
        transitiveSelfTestEntity.setName("Test TransitiveSelfEntity");
        when(entityIdentifierDao.getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter))).thenReturn(transitiveSelfTestEntity);

        TransitiveSelfTestEntity result = testTransitiveSelfEntityDao.getTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);

        assertEquals(1L, result.getId());
        assertEquals("Test TransitiveSelfEntity", result.getName());
    }

    @Test
    void getTransitiveSelfEntity_Failure() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter));

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntity(parameter);
        });
    }

    @Test
    void getTransitiveSelfEntityWith_Failure() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntity(eq(TransitiveSelfTestEntity.class), eq(parameter));

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);
        });
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityTreeDataSet.yml")
    void getTransitiveSelfEntitiesTree_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("root");
        try {
            Field sessionManagerField = TransitiveSelfTestEntity.class.getDeclaredField("parent");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(root, parent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("child");
        try {
            Field sessionManagerField = TransitiveSelfTestEntity.class.getDeclaredField("parent");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(child, root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Field sessionManagerField = TransitiveSelfTestEntity.class.getDeclaredField("childNodeList");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(parent, List.of(root));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Field sessionManagerField = TransitiveSelfTestEntity.class.getDeclaredField("childNodeList");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(root, List.of(child));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Map<TransitiveSelfEnum, List<TransitiveSelfEntity>> expectedMap = new HashMap<>() {{
            put(TransitiveSelfEnum.PARENT, List.of(parent));
            put(TransitiveSelfEnum.ROOT, List.of(root));
            put(TransitiveSelfEnum.CHILD, List.of(child));
        }};
        when(entityIdentifierDao.getEntityList(eq(TransitiveSelfTestEntity.class))).thenReturn(List.of(parent, root, child));

        Map<TransitiveSelfEnum, List<TransitiveSelfEntity>> result = testTransitiveSelfEntityDao.getTransitiveSelfEntitiesTree();

        assertEquals(expectedMap.get(TransitiveSelfEnum.PARENT), result.get(TransitiveSelfEnum.PARENT));
        assertEquals(expectedMap.get(TransitiveSelfEnum.ROOT), result.get(TransitiveSelfEnum.ROOT));
        assertEquals(expectedMap.get(TransitiveSelfEnum.CHILD), result.get(TransitiveSelfEnum.CHILD));
    }


}
