package com.tm.core.test.dao;

import com.tm.core.dao.basic.TestTransitiveSelfEntityDao;
import com.tm.core.process.dao.identifier.QueryService;
import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.process.dao.transitive.AbstractTransitiveSelfEntityDao;
import com.tm.core.modal.transitive.TransitiveSelfEntity;
import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.table.EntityTable;
import com.tm.core.util.TransitiveSelfEnum;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransitiveSelfEntityDaoTest extends AbstractDaoTest {

    private static IQueryService queryService;

    private static TestTransitiveSelfEntityDao testTransitiveSelfEntityDao;

    @BeforeAll
    public static void setUpAll() {
        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(new EntityTable(TransitiveSelfEntity.class, "transitiveselfentity"));
        queryService = new QueryService(entityMappingManager);
        testTransitiveSelfEntityDao = new TestTransitiveSelfEntityDao(sessionFactory, queryService);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field queryServiceField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("queryService");
            queryServiceField.setAccessible(true);
            queryServiceField.set(testTransitiveSelfEntityDao, queryService);

            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE transitiveselfentity RESTART IDENTITY").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh table: " + e.getMessage(), e);
        }

    }

    @Test
    void getTransitiveSelfEntityList_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 2L);

        List<TransitiveSelfEntity> result = testTransitiveSelfEntityDao.getTransitiveSelfEntityList(parameter);

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
    void saveEntityTree_success() {
        loadDataSet("/datasets/transitive_self/emptyTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        testTransitiveSelfEntityDao.saveEntityTree(root);
        verifyExpectedData("/datasets/transitive_self/saveTransitiveSelfEntityDataSet.yml");
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

        TransitiveSelfEntity transitiveSelfEntity = new TransitiveSelfEntity();
        transitiveSelfEntity.setName("Entity");

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(any(TransitiveSelfEntity.class));

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.saveEntityTree(transitiveSelfEntity);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void updateEntityTreeOldMain_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfEntity updParent = new TransitiveSelfEntity();
        updParent.setName("Update Parent");

        TransitiveSelfEntity updRoot = new TransitiveSelfEntity();
        updRoot.setName("Update Root");

        TransitiveSelfEntity updChild = new TransitiveSelfEntity();
        updChild.setName("Update Child");

        updParent.addChildTransitiveEntity(updRoot);
        updRoot.addChildTransitiveEntity(updChild);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
        verifyExpectedData("/datasets/transitive_self/updateTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void updateEntityTreeOldMainTwoChildren_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfEntity updParent = new TransitiveSelfEntity();
        updParent.setName("Update Parent");

        TransitiveSelfEntity updRoot = new TransitiveSelfEntity();
        updRoot.setName("Update Root");

        TransitiveSelfEntity updChild1 = new TransitiveSelfEntity();
        updChild1.setName("Update Child");
        TransitiveSelfEntity updChild2 = new TransitiveSelfEntity();
        updChild2.setName("Update Child");

        updParent.addChildTransitiveEntity(updRoot);
        updRoot.addChildTransitiveEntity(updChild1);
        updRoot.addChildTransitiveEntity(updChild2);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
        verifyExpectedData("/datasets/transitive_self/updateTwoChildrenTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void updateEntityTreeOldMainNoChildren_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfEntity updParent = new TransitiveSelfEntity();
        updParent.setName("Update Parent");

        TransitiveSelfEntity updRoot = new TransitiveSelfEntity();
        updRoot.setName("Update Root");

        updParent.addChildTransitiveEntity(updRoot);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
        verifyExpectedData("/datasets/transitive_self/updateNoChildrenTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void updateEntityTreeOldMainNoParentNoChildren_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        TransitiveSelfEntity updRoot = new TransitiveSelfEntity();
        updRoot.setName("Update Root");

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.updateEntityTreeOldMain(updRoot, parameter);
        verifyExpectedData("/datasets/transitive_self/updateNoParentNoChildrenTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void updateEntityTreeOldMain_transactionFailure() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        NativeQuery<TransitiveSelfEntity> query = mock(NativeQuery.class);
        IQueryService queryService = mock(IQueryService.class);

        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TransitiveSelfEntity transitiveSelfEntity = new TransitiveSelfEntity();
        transitiveSelfEntity.setName("Entity");

        Parameter parameter = new Parameter("id", 2L);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createNativeQuery(anyString(), eq(TransitiveSelfEntity.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(transitiveSelfEntity);
        doNothing().when(session).remove(any(TransitiveSelfEntity.class));
        doThrow(new RuntimeException()).when(session).merge(transitiveSelfEntity);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.updateEntityTreeOldMain(transitiveSelfEntity, parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
    }

    @Test
    void deleteParentEntityTree_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        Parameter parameter = new Parameter("id", 1L);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
        verifyExpectedData("/datasets/transitive_self/emptyTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void addEntityToChildList_success() {
        loadDataSet("/datasets/transitive_self/updateTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity addChild = new TransitiveSelfEntity();
        addChild.setName("Update Child");

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.addEntityToChildList(addChild, parameter);
        verifyExpectedData("/datasets/transitive_self/updateTwoChildrenTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void deleteRootEntityTree_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        Parameter parameter = new Parameter("id", 2L);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
        verifyExpectedData("/datasets/transitive_self/deleteRootTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void deleteChildEntityTree_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        TransitiveSelfEntity parent = new TransitiveSelfEntity();
        parent.setId(1L);
        parent.setName("Parent");

        TransitiveSelfEntity root = new TransitiveSelfEntity();
        root.setId(2L);
        root.setName("Root");

        TransitiveSelfEntity child = new TransitiveSelfEntity();
        child.setId(3L);
        child.setName("Child");

        parent.addChildTransitiveEntity(root);
        root.addChildTransitiveEntity(child);

        Parameter parameter = new Parameter("id", 3L);

        testTransitiveSelfEntityDao.deleteEntityTree(parameter);
        verifyExpectedData("/datasets/transitive_self/deleteChildTransitiveSelfEntityDataSet.yml");
    }

    @Test
    void deleteEntityTree_transactionFailure() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        NativeQuery<TransitiveSelfEntity> query = mock(NativeQuery.class);

        try {
            Field sessionManagerField = AbstractTransitiveSelfEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testTransitiveSelfEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TransitiveSelfEntity parent = new TransitiveSelfEntity();

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createNativeQuery(anyString(), eq(TransitiveSelfEntity.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(parent);
        doThrow(new RuntimeException()).when(session).remove(any(TransitiveSelfEntity.class));

        Parameter parameter = new Parameter("id", 2L);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.deleteEntityTree(parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    void getOptionalTransitiveSelfEntity_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 2L);

        Optional<TransitiveSelfEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);

        assertTrue(result.isPresent());
        TransitiveSelfEntity resultEntity = result.get();
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
    void getOptionalTransitiveSelfEntity_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        Optional<TransitiveSelfEntity> result = testTransitiveSelfEntityDao.getOptionalTransitiveSelfEntity(parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransitiveSelfEntity_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 2L);

        TransitiveSelfEntity result = testTransitiveSelfEntityDao.getTransitiveSelfEntity(parameter);

        assertEquals(2L, result.getId());
        assertEquals("Root", result.getName());
        TransitiveSelfEntity parent = result.getParent();
        assertEquals(1L, parent.getId());
        assertEquals("Parent", result.getParent().getRootField());
        TransitiveSelfEntity child = (TransitiveSelfEntity) result.getChildNodeList().get(0);
        assertEquals(3L, child.getId());
        assertEquals("Child", child.getRootField());
    }

    @Test
    void getTransitiveSelfEntity_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testTransitiveSelfEntityDao.getTransitiveSelfEntity(parameter);
        });
    }

    @Test
    void getTransitiveSelfEntitiesTree_success() {
        loadDataSet("/datasets/transitive_self/testTransitiveSelfEntityTreeDataSet.yml");
        Map<TransitiveSelfEnum, List<com.tm.core.modal.TransitiveSelfEntity>> result = testTransitiveSelfEntityDao.getTransitiveSelfEntitiesTree();

        assertEquals("Parent", result.get(TransitiveSelfEnum.PARENT).get(0).getRootField());
        assertEquals("Root", result.get(TransitiveSelfEnum.ROOT).get(0).getRootField());
        assertEquals("Child", result.get(TransitiveSelfEnum.CHILD).get(0).getRootField());
    }


}
