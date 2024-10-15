package com.tm.core.test.dao;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.AbstractEntityDao;
import com.tm.core.dao.basic.TestSingleEntityDao;
import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.single.AbstractSingleEntityDao;
import com.tm.core.modal.single.SingleTestEntity;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.table.EntityTable;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
class SingleEntityDaoTest {

    protected static IThreadLocalSessionManager sessionManager;
    protected static SessionFactory sessionFactory;

    public static TestSingleEntityDao testSingleEntityDao;

    public static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        sessionFactory = getSessionFactory();
        sessionManager = new ThreadLocalSessionManager(sessionFactory);

        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(new EntityTable(SingleTestEntity.class, "singletestentity"));
        IEntityIdentifierDao entityIdentifierDao = new EntityIdentifierDao(sessionManager, entityMappingManager);
        testSingleEntityDao = new TestSingleEntityDao(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionFactoryField = AbstractSingleEntityDao.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(testSingleEntityDao, sessionFactory);

            Field sessionManagerField = AbstractSingleEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testSingleEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntityList_success() {
        Parameter parameter = new Parameter("id", 1L);

        List<SingleTestEntity> result = testSingleEntityDao.getEntityList(parameter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Entity 1", result.get(0).getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntityListWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        List<SingleTestEntity> result = testSingleEntityDao.getEntityList(SingleTestEntity.class, parameter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Entity 1", result.get(0).getName());
    }

    @Test
    void getEntityList_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.getEntityList(parameter);
        });

    }

    @Test
    void getEntityListWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.getEntityList(SingleTestEntity.class, parameter);
        });

    }

    @Test
    @DataSet("/datasets/single/emptySingleEntityDataSet.yml")
    @ExpectedDataSet("/datasets/single/saveSingleEntityDataSet.yml")
    void saveEntity_success() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setName("New Entity");

        testSingleEntityDao.saveEntity(singleTestEntity);
    }

    @Test
    void saveEntity_transactionFailure() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setName("New Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(singleTestEntity);

        try {
            Field sessionManagerField = AbstractSingleEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testSingleEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.saveEntity(singleTestEntity);
        });

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

        testSingleEntityDao.updateEntity(singleTestEntity);
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
            Field sessionManagerField = AbstractSingleEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testSingleEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(singleTestEntity);

        assertThrows(RuntimeException.class, () -> testSingleEntityDao.updateEntity(singleTestEntity));
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/emptySingleEntityDataSet.yml")
    void deleteEntity_success() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(1);

        testSingleEntityDao.deleteEntity(singleTestEntity);
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/testSingleEntityDataSet.yml")
    void deleteEntity_transactionFailure() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(100);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.deleteEntity(singleTestEntity);
        });

    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/updateSingleEntityDataSet.yml")
    void findEntityAndUpdate_success() {
        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity updateTestEntity = new SingleTestEntity();
        updateTestEntity.setId(1L);
        updateTestEntity.setName("Update Entity");

        testSingleEntityDao.findEntityAndUpdate(updateTestEntity, parameter);
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void findEntityAndUpdate_transactionFailure() {
        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        try {
            Field sessionManagerField = AbstractSingleEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testSingleEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setName("Update Entity");

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(singleTestEntity);

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.findEntityAndUpdate(singleTestEntity, parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(sessionManager).closeSession();
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/emptySingleEntityDataSet.yml")
    void findEntityAndDelete_success() {
        Parameter parameter = new Parameter("id", 1L);

        testSingleEntityDao.findEntityAndDelete(parameter);
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void findEntityAndDelete_transactionFailure() {
        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        try {
            Field sessionManagerField = AbstractSingleEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testSingleEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Parameter parameter = new Parameter("id", 1L);

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(SingleTestEntity.class));

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.findEntityAndDelete(parameter);
        });

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(sessionManager).closeSession();
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    @ExpectedDataSet("datasets/single/updateSingleEntityDataSet.yml")
    void mutateEntity_success() {
        String sql = "UPDATE singleTestEntity SET name = ? WHERE id = ?";

        Parameter[] parameters = new Parameter[]{
                new Parameter("name", "Update Entity"),
                new Parameter("id", 1L)
        };

        testSingleEntityDao.mutateEntity(sql, parameters);
    }

    @Test
    void mutateEntity_transactionFailure() {
        String sqlQuery = "UPDATE testentity SET name = ? WHERE id = ?";

        Parameter[] parameters = new Parameter[]{
                new Parameter("name", "Update Entity"),
                new Parameter("id", 100L)
        };

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.mutateEntity(sqlQuery, parameters);
        });

    }

    @Test
    @DataSet(value = "datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        Optional<SingleTestEntity> result = testSingleEntityDao.getOptionalEntity(parameter);

        assertTrue(result.isPresent());
        SingleTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Test Entity 1", resultEntity.getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntity_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.getOptionalEntity(parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void getOptionalEntity_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        Optional<SingleTestEntity> result = testSingleEntityDao.getOptionalEntity(parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    @DataSet("/datasets/single/testSingleEntityDataSet.yml")
    void getEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity result = testSingleEntityDao.getEntity(parameter);

        assertEquals(1L, result.getId());
        assertEquals("Test Entity 1", result.getName());
    }

    @Test
    void getEntity_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.getEntity(parameter);
        });

    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        Optional<SingleTestEntity> result = testSingleEntityDao.getOptionalEntity(SingleTestEntity.class, parameter);

        assertTrue(result.isPresent());
        SingleTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Test Entity 1", resultEntity.getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntityWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.getOptionalEntity(SingleTestEntity.class, parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntityWithClass_NoResult() {
        Parameter parameter = new Parameter("id", 100L);

        Optional<SingleTestEntity> result = testSingleEntityDao.getOptionalEntity(SingleTestEntity.class, parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity result = testSingleEntityDao.getEntity(SingleTestEntity.class, parameter);

        assertEquals(1L, result.getId());
        assertEquals("Test Entity 1", result.getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntityWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 100L);

        assertThrows(RuntimeException.class, () -> {
            testSingleEntityDao.getEntity(SingleTestEntity.class, parameter);
        });

    }

    private static class TestSingleEntityDaoExposed extends AbstractEntityDao {
        public TestSingleEntityDaoExposed() {
            super(SingleTestEntity.class);
        }

        @Override
        public <E> void classTypeChecker(E entity) {
            super.classTypeChecker(entity);
        }
    }

    @Test
    void classTypeChecker_withMatchingTypes_shouldNotThrowException() {
        SingleTestEntity singleTestEntity = new SingleTestEntity();
        TestSingleEntityDaoExposed singleEntityDao = new TestSingleEntityDaoExposed();

        assertDoesNotThrow(
                () -> singleEntityDao.classTypeChecker(singleTestEntity));
    }

    @Test
    void classTypeChecker_withNonMatchingTypes_shouldThrowException() {
        Object object = new Object();
        TestSingleEntityDaoExposed singleEntityDao
                = new TestSingleEntityDaoExposed();

        assertThrows(RuntimeException.class, () ->
                singleEntityDao.classTypeChecker(object)
        );
    }
}
