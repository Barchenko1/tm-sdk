package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.basic.TestTransitiveSelfEntityDao;
import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.transitive.AbstractTransitiveSelfEntityDao;
import com.tm.core.modal.transitive.TransitiveSelfTestEntity;
import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.table.EntityTable;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import com.tm.core.util.TransitiveSelfEnum;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
class TransitiveSelfEntityDaoTest {

    private static IThreadLocalSessionManager sessionManager;
    private static SessionFactory sessionFactory;
    private static IEntityIdentifierDao entityIdentifierDao;

    private static TestTransitiveSelfEntityDao testTransitiveSelfEntityDao;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        sessionFactory = getSessionFactory();
        sessionManager = new ThreadLocalSessionManager(sessionFactory);

        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(new EntityTable(TransitiveSelfTestEntity.class, "transitiveselftestentity"));
        entityIdentifierDao = new EntityIdentifierDao(sessionManager, entityMappingManager);
        testTransitiveSelfEntityDao = new TestTransitiveSelfEntityDao(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field entityIdentifierDaoField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
            entityIdentifierDaoField.setAccessible(true);
            entityIdentifierDaoField.set(testTransitiveSelfEntityDao, entityIdentifierDao);

            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, sessionFactory);

            Field sessionManagerField2 = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField2.setAccessible(true);
            sessionManagerField2.set(testTransitiveSelfEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE transitiveselftestentity RESTART IDENTITY").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh table: " + e.getMessage(), e);
        }

    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getTransitiveSelfEntityList_success() {
        Parameter parameter = new Parameter("id", 2L);

        List<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getTransitiveSelfEntityList(parameter);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("Root", result.get(0).getName());
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getTransitiveSelfEntityListWithClass_success() {
        Parameter parameter = new Parameter("id", 2L);

        List<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getTransitiveSelfEntityList(TransitiveSelfTestEntity.class, parameter);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("Root", result.get(0).getName());
    }

    @Test
    void getTransitiveSelfEntityList_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntityList(parameter);
        });
    }

    @Test
    void getTransitiveSelfEntityListWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntityList(TransitiveSelfTestEntity.class, parameter);
        });
    }

    @Test
    @DataSet("datasets/transitive_self/emptyTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/saveTransitiveSelfEntityDataSet.yml")
    void saveEntityTree_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        testTransitiveSelfEntityDao.saveEntityTree(root);
    }

    @Test
    void saveEntityTree_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionFactoryField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(testTransitiveSelfEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setName("Entity");

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(any(TransitiveSelfTestEntity.class));

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
    void updateEntityTreeOldMain_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfTestEntity updParent = new TransitiveSelfTestEntity();
        updParent.setName("Update Parent");

        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
        updRoot.setName("Update Root");

        TransitiveSelfTestEntity updChild = new TransitiveSelfTestEntity();
        updChild.setName("Update Child");

        updParent.addChildTransitiveEntity(updRoot);
        updRoot.addChildTransitiveEntity(updChild);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/updateTwoChildrenTransitiveSelfEntityDataSet.yml")
    void updateEntityTreeOldMainTwoChildren_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfTestEntity updParent = new TransitiveSelfTestEntity();
        updParent.setName("Update Parent");

        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
        updRoot.setName("Update Root");

        TransitiveSelfTestEntity updChild1 = new TransitiveSelfTestEntity();
        updChild1.setName("Update Child");
        TransitiveSelfTestEntity updChild2 = new TransitiveSelfTestEntity();
        updChild2.setName("Update Child");

        updParent.addChildTransitiveEntity(updRoot);
        updRoot.addChildTransitiveEntity(updChild1);
        updRoot.addChildTransitiveEntity(updChild2);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/updateNoChildrenTransitiveSelfEntityDataSet.yml")
    void updateEntityTreeOldMainNoChildren_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfTestEntity updParent = new TransitiveSelfTestEntity();
        updParent.setName("Update Parent");

        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
        updRoot.setName("Update Root");

        updParent.addChildTransitiveEntity(updRoot);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/updateNoParentNoChildrenTransitiveSelfEntityDataSet.yml")
    void updateEntityTreeOldMainNoParentNoChildren_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
        updRoot.setName("Update Root");

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void updateEntityTreeOldMain_transactionFailure() {
        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        IEntityIdentifierDao entityIdentifierDao = mock(IEntityIdentifierDao.class);

        try {
            Field entityIdentifierDaoField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
            entityIdentifierDaoField.setAccessible(true);
            entityIdentifierDaoField.set(testTransitiveSelfEntityDao, entityIdentifierDao);

            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
        transitiveSelfTestEntity.setName("Entity");

        Parameter parameter = new Parameter("id", 2L);

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(entityIdentifierDao.getEntity(TransitiveSelfTestEntity.class, parameter)).thenReturn(transitiveSelfTestEntity);
        doNothing().when(session).remove(any(TransitiveSelfTestEntity.class));
        doThrow(new RuntimeException()).when(session).merge(transitiveSelfTestEntity);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.updateEntityTreeOldMain(transitiveSelfTestEntity, parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(sessionManager).closeSession();
    }

    // fix tests or more to grape design
//    @Test
//    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
//    @ExpectedDataSet("datasets/transitive_self/updateTransitiveSelfEntityDataSet.yml")
//    void updateEntityTreeNewMain_success() {
//        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
//        parent.setId(1L);
//        parent.setName("Parent");
//
//        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
//        root.setId(2L);
//        root.setName("Root");
//
//        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
//        child.setId(3L);
//        child.setName("Child");
//
//        parent.addChildTransitiveEntity(root);
//        root.addChildTransitiveEntity(child);
//
//        TransitiveSelfTestEntity updParent = new TransitiveSelfTestEntity();
//        updParent.setName("Update Parent");
//
//        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
//        updRoot.setName("Update Root");
//
//        TransitiveSelfTestEntity updChild = new TransitiveSelfTestEntity();
//        updChild.setName("Update Child");
//
//        updParent.addChildTransitiveEntity(updRoot);
//        updRoot.addChildTransitiveEntity(updChild);
//
//        Parameter parameter = new Parameter("id", 2L);
//
//        testTransitiveSelfEntityDao.updateEntityTreeNewMain(updRoot, parameter);
//    }
//
//    @Test
//    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
//    @ExpectedDataSet("datasets/transitive_self/updateTwoChildrenTransitiveSelfEntityDataSet.yml")
//    void updateEntityTreeNewMainTwoChildren_success() {
//        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
//        parent.setId(1L);
//        parent.setName("Parent");
//
//        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
//        root.setId(2L);
//        root.setName("Root");
//
//        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
//        child.setId(3L);
//        child.setName("Child");
//
//        parent.addChildTransitiveEntity(root);
//        root.addChildTransitiveEntity(child);
//
//        TransitiveSelfTestEntity updParent = new TransitiveSelfTestEntity();
//        updParent.setName("Update Parent");
//
//        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
//        updRoot.setName("Update Root");
//
//        TransitiveSelfTestEntity updChild1 = new TransitiveSelfTestEntity();
//        updChild1.setName("Update Child");
//        TransitiveSelfTestEntity updChild2 = new TransitiveSelfTestEntity();
//        updChild2.setName("Update Child");
//
//        updParent.addChildTransitiveEntity(updRoot);
//        updRoot.addChildTransitiveEntity(updChild1);
//        updRoot.addChildTransitiveEntity(updChild2);
//
//        Parameter parameter = new Parameter("id", 2L);
//
//        testTransitiveSelfEntityDao.updateEntityTreeNewMain(updRoot, parameter);
//    }
//
//    @Test
//    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
//    @ExpectedDataSet("datasets/transitive_self/updateNoChildrenTransitiveSelfEntityDataSet.yml")
//    void updateEntityTreeNewMainNoChildren_success() {
//        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
//        parent.setId(1L);
//        parent.setName("Parent");
//
//        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
//        root.setId(2L);
//        root.setName("Root");
//
//        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
//        child.setId(3L);
//        child.setName("Child");
//
//        parent.addChildTransitiveEntity(root);
//        root.addChildTransitiveEntity(child);
//
//        TransitiveSelfTestEntity updParent = new TransitiveSelfTestEntity();
//        updParent.setName("Update Parent");
//
//        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
//        updRoot.setName("Update Root");
//
//        updParent.addChildTransitiveEntity(updRoot);
//
//        Parameter parameter = new Parameter("id", 2L);
//
//        testTransitiveSelfEntityDao.updateEntityTreeNewMain(updRoot, parameter);
//    }
//
//    @Test
//    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
//    @ExpectedDataSet("datasets/transitive_self/updateNoParentNoChildrenTransitiveSelfEntityDataSet.yml")
//    void updateEntityTreeNewMainNoParentNoChildren_success() {
//        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
//        parent.setId(1L);
//        parent.setName("Parent");
//
//        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
//        root.setId(2L);
//        root.setName("Root");
//
//        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
//        child.setId(3L);
//        child.setName("Child");
//
//        parent.addChildTransitiveEntity(root);
//        root.addChildTransitiveEntity(child);
//
//        TransitiveSelfTestEntity updRoot = new TransitiveSelfTestEntity();
//        updRoot.setName("Update Root");
//
//        Parameter parameter = new Parameter("id", 2L);
//
//        testTransitiveSelfEntityDao.updateEntityTreeNewMain(updRoot, parameter);
//    }
//
//    @Test
//    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
//    void updateEntityTreeNewMain_transactionFailure() {
//        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
//        Session session = mock(Session.class);
//        Transaction transaction = mock(Transaction.class);
//        IEntityIdentifierDao entityIdentifierDao = mock(IEntityIdentifierDao.class);
//
//        try {
//            Field entityIdentifierDaoField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("entityIdentifierDao");
//            entityIdentifierDaoField.setAccessible(true);
//            entityIdentifierDaoField.set(testTransitiveSelfEntityDao, entityIdentifierDao);
//
//            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionManager");
//            sessionManagerField.setAccessible(true);
//            sessionManagerField.set(testTransitiveSelfEntityDao, sessionManager);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        TransitiveSelfTestEntity transitiveSelfTestEntity = new TransitiveSelfTestEntity();
//        transitiveSelfTestEntity.setName("Entity");
//
//        Parameter parameter = new Parameter("id", 2L);
//
//        when(sessionManager.getSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//        when(entityIdentifierDao.getEntity(TransitiveSelfTestEntity.class, parameter)).thenReturn(transitiveSelfTestEntity);
//        doNothing().when(session).remove(any(TransitiveSelfTestEntity.class));
//        doThrow(new RuntimeException()).when(session).merge(transitiveSelfTestEntity);
//
//        assertThrows(RuntimeException.class, () -> {
//            testTransitiveSelfEntityDao.updateEntityTreeNewMain(transitiveSelfTestEntity, parameter);
//        });
//
//        verify(transaction).rollback();
//        verify(transaction, never()).commit();
//        verify(sessionManager).closeSession();
//    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/emptyTransitiveSelfEntityDataSet.yml")
    void deleteParentEntityTree_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        Parameter parameter = new Parameter("id", 1L);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
    }

    @Test
    @DataSet(value = "datasets/transitive_self/updateTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/updateTwoChildrenTransitiveSelfEntityDataSet.yml")
    void addEntityToChildList_success() {
        TransitiveSelfTestEntity addChild = new TransitiveSelfTestEntity();
        addChild.setName("Update Child");

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.addEntityToChildList(addChild, parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/deleteRootTransitiveSelfEntityDataSet.yml")
    void deleteRootEntityTree_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    @ExpectedDataSet("datasets/transitive_self/deleteChildTransitiveSelfEntityDataSet.yml")
    void deleteChildEntityTree_success() {
        TransitiveSelfTestEntity parent = new TransitiveSelfTestEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfTestEntity root = new TransitiveSelfTestEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfTestEntity child = new TransitiveSelfTestEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        Parameter parameter = new Parameter("id", 3L);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void deleteEntityTree_transactionFailure() {
        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(TransitiveSelfTestEntity.class));

        Parameter parameter = new Parameter("id", 2L);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.deleteEntityTree(parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(sessionManager).closeSession();
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getOptionalTransitiveSelfEntity_success() {
        Parameter parameter = new Parameter("id", 2L);

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);

        assertTrue(result.isPresent());
        TransitiveSelfTestEntity resultEntity = result.get();
        assertEquals(2L, resultEntity.getId());
        assertEquals("Root", resultEntity.getName());
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getOptionalTransitiveSelfEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 2L);

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);

        assertTrue(result.isPresent());
        TransitiveSelfTestEntity resultEntity = result.get();
        assertEquals(2L, resultEntity.getId());
        assertEquals("Root", resultEntity.getName());
    }

    @Test
    void getOptionalTransitiveSelfEntity_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);
        });
    }

    @Test
    void getOptionalTransitiveSelfEntityWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);
        });
    }

    @Test
    void getOptionalTransitiveSelfEntity_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        Optional<TransitiveSelfTestEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    void getOptionalTransitiveSelfEntityWithClass_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        Optional<TransitiveSelfTestEntity> result =
                testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getTransitiveSelfEntity_success() {
        Parameter parameter = new Parameter("id", 2L);

        TransitiveSelfTestEntity result = testTransitiveSelfEntityDao.getTransitiveSelfEntity(parameter);

        assertEquals(2L, result.getId());
        assertEquals("Root", result.getName());
        TransitiveSelfTestEntity parent = result.getParent();
        assertEquals(1L, parent.getId());
        assertEquals("Parent", result.getParent().getRootField());
        TransitiveSelfTestEntity child = (TransitiveSelfTestEntity) result.getChildNodeList().get(0);
        assertEquals(3L, child.getId());
        assertEquals("Child", child.getRootField());
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityDataSet.yml")
    void getTransitiveSelfEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 2L);

        TransitiveSelfTestEntity result = testTransitiveSelfEntityDao.getTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);

        assertEquals(2L, result.getId());
        assertEquals("Root", result.getName());
    }

    @Test
    void getTransitiveSelfEntity_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntity(parameter);
        });
    }

    @Test
    void getTransitiveSelfEntityWith_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntity(TransitiveSelfTestEntity.class, parameter);
        });
    }

    @Test
    @DataSet("datasets/transitive_self/testTransitiveSelfEntityTreeDataSet.yml")
    void getTransitiveSelfEntitiesTree_success() {
        Map<TransitiveSelfEnum, List<TransitiveSelfEntity>> result = testTransitiveSelfEntityDao.getTransitiveSelfEntitiesTree();

        assertEquals("Parent", result.get(TransitiveSelfEnum.PARENT).get(0).getRootField());
        assertEquals("Root", result.get(TransitiveSelfEnum.ROOT).get(0).getRootField());
        assertEquals("Child", result.get(TransitiveSelfEnum.CHILD).get(0).getRootField());
    }


}
