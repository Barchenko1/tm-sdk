package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.basic.TestEntityDao;
import com.tm.core.dao.common.AbstractEntityDao;
import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.relationship.DependentTestEntity;
import com.tm.core.modal.relationship.RelationshipRootTestEntity;
import com.tm.core.modal.relationship.SingleDependentTestEntity;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.table.EntityTable;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
public class TestEntityDaoTest {
    private static IThreadLocalSessionManager sessionManager;
    private static SessionFactory sessionFactory;

    private static TestEntityDao testEntityDao;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        sessionFactory = getSessionFactory();
        sessionManager = new ThreadLocalSessionManager(sessionFactory);

        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        IEntityIdentifierDao entityIdentifierDao = new EntityIdentifierDao(sessionManager, entityMappingManager);
        testEntityDao = new TestEntityDao(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionFactoryField = AbstractEntityDao.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(testEntityDao, sessionFactory);

            Field sessionManagerField = AbstractEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static IEntityMappingManager getEntityMappingManager() {
        EntityTable dependentTestEntity = new EntityTable(DependentTestEntity.class, "dependentTestEntity");
        EntityTable singleDependentTestEntity = new EntityTable(SingleDependentTestEntity.class, "singleDependentTestEntity");
        EntityTable relationshipRootTestEntity = new EntityTable(RelationshipRootTestEntity.class, "relationshipRootTestEntity");

        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(dependentTestEntity);
        entityMappingManager.addEntityTable(singleDependentTestEntity);
        entityMappingManager.addEntityTable(relationshipRootTestEntity);
        return entityMappingManager;
    }

    private RelationshipRootTestEntity prepareToSaveRelationshipRootTestEntity() {
        SingleDependentTestEntity singleDependentTestEntity = new SingleDependentTestEntity();
        singleDependentTestEntity.setName("Single Dependent Entity");

        DependentTestEntity dependentTestEntity1 = new DependentTestEntity();
        dependentTestEntity1.setName("Dependent Entity");
        DependentTestEntity dependentTestEntity2 = new DependentTestEntity();
        dependentTestEntity2.setName("Dependent Entity");

        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("Relationship Root Entity");
        relationshipRootTestEntity.setSingleDependentTestEntity(singleDependentTestEntity);
        relationshipRootTestEntity.setDependentTestEntityList(Arrays.asList(dependentTestEntity1, dependentTestEntity2));

        return relationshipRootTestEntity;
    }

    private RelationshipRootTestEntity prepareToUpdateRelationshipRootTestEntity() {
        SingleDependentTestEntity singleDependentTestEntity = new SingleDependentTestEntity();
        singleDependentTestEntity.setId(1L);
        singleDependentTestEntity.setName("Updated Single Dependent Entity");

        DependentTestEntity dependentTestEntity1 = new DependentTestEntity();
        dependentTestEntity1.setId(1L);
        dependentTestEntity1.setName("Updated Dependent Entity");
        DependentTestEntity dependentTestEntity2 = new DependentTestEntity();
        dependentTestEntity2.setId(2L);
        dependentTestEntity2.setName("Updated Dependent Entity");

        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setId(1L);
        relationshipRootTestEntity.setName("Updated Relationship Root Entity");
        relationshipRootTestEntity.setSingleDependentTestEntity(singleDependentTestEntity);
        relationshipRootTestEntity.setDependentTestEntityList(Arrays.asList(dependentTestEntity1, dependentTestEntity2));

        return relationshipRootTestEntity;
    }

    private RelationshipRootTestEntity prepareRelationshipRootTestEntityDbMock() {
        SingleDependentTestEntity singleDependentTestEntity = new SingleDependentTestEntity();
        singleDependentTestEntity.setId(1L);
        singleDependentTestEntity.setName("Single Dependent Entity");

        DependentTestEntity dependentTestEntity1 = new DependentTestEntity();
        dependentTestEntity1.setId(1L);
        dependentTestEntity1.setName("Dependent Entity");
        DependentTestEntity dependentTestEntity2 = new DependentTestEntity();
        dependentTestEntity2.setId(2L);
        dependentTestEntity2.setName("Dependent Entity");

        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setId(1L);
        relationshipRootTestEntity.setName("Relationship Root Entity");
        relationshipRootTestEntity.setSingleDependentTestEntity(singleDependentTestEntity);
        relationshipRootTestEntity.setDependentTestEntityList(Arrays.asList(dependentTestEntity1, dependentTestEntity2));

        return relationshipRootTestEntity;
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/saveSingleRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntity_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("Relationship Root Entity");

        testEntityDao.persistEntity(relationshipRootTestEntity);
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntityTypeCheck_success() {
        Object object = new Object();
        assertThrows(RuntimeException.class, () -> {
            testEntityDao.persistEntity(object);
        });
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/saveMultipleRelationshipTestEntityDataSet.yml")
    void saveEntityWithDependencies_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareToSaveRelationshipRootTestEntity();

        testEntityDao.persistEntity(relationshipRootTestEntity);
    }

    @Test
    void saveRelationshipEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setId(1L);
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(relationshipRootTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.persistEntity(relationshipRootTestEntity);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/saveSingleRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntitySupplier_success() {
        Supplier<RelationshipRootTestEntity> supplier = () -> {
            RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
            relationshipRootTestEntity.setName("Relationship Root Entity");
            return relationshipRootTestEntity;
        };

        testEntityDao.saveEntity(supplier);
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntitySupplier_transactionFailure() {
        Supplier<RelationshipRootTestEntity> supplier = () -> {
            RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
            relationshipRootTestEntity.setName("New RelationshipRootTestEntity");
            throw new RuntimeException();
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.saveEntity(supplier);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/saveSingleRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntityConsumer_success() {
        Consumer<Session> consumer = (Session s) -> {
            RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
            relationshipRootTestEntity.setName("Relationship Root Entity");
            s.persist(relationshipRootTestEntity);
        };

        testEntityDao.executeConsumer(consumer);
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntityConsumer_transactionFailure() {
        Consumer<Session> consumer = (Session s) -> {
            RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
            relationshipRootTestEntity.setName("New RelationshipRootTestEntity");
            s.persist(relationshipRootTestEntity);
            throw new RuntimeException();
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.executeConsumer(consumer);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/updateRelationshipTestEntityDataSet.yml")
    void updateEntity_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareToUpdateRelationshipRootTestEntity();
        testEntityDao.mergeEntity(relationshipRootTestEntity);
    }

    @Test
    void updateEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setId(100L);
        relationshipRootTestEntity.setName("Update Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(relationshipRootTestEntity);

        assertThrows(RuntimeException.class, () -> testEntityDao.mergeEntity(relationshipRootTestEntity));
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/updateRelationshipTestEntityDataSet.yml")
    void updateRelationshipEntity_success() {
        Supplier<RelationshipRootTestEntity> relationshipEntitySupplier = () -> {
            RelationshipRootTestEntity oldRelationShipEntity = prepareRelationshipRootTestEntityDbMock();
            RelationshipRootTestEntity relationshipRootTestEntityToUpdate = prepareToUpdateRelationshipRootTestEntity();
            relationshipRootTestEntityToUpdate.setId(oldRelationShipEntity.getId());
            relationshipRootTestEntityToUpdate.getSingleDependentTestEntity()
                    .setId(oldRelationShipEntity.getSingleDependentTestEntity().getId());
            relationshipRootTestEntityToUpdate.getDependentTestEntityList().get(0)
                    .setId(oldRelationShipEntity.getDependentTestEntityList().get(0).getId());
            relationshipRootTestEntityToUpdate.getDependentTestEntityList().get(1)
                    .setId(oldRelationShipEntity.getDependentTestEntityList().get(1).getId());
            return relationshipRootTestEntityToUpdate;
        };
        testEntityDao.updateEntity(relationshipEntitySupplier);
    }

    @Test
    void updateRelationshipEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareToSaveRelationshipRootTestEntity();
        relationshipRootTestEntity.setId(1L);

        Supplier<RelationshipRootTestEntity> relationshipRootTestEntitySupplier = () -> {
            throw new RuntimeException();
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.updateEntity(relationshipRootTestEntitySupplier);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/updateRelationshipTestEntityDataSet.yml")
    void updateRelationshipEntityConsumer_success() {
        Consumer<Session> consumer = (Session s) -> {
            RelationshipRootTestEntity oldRelationShipEntity = prepareRelationshipRootTestEntityDbMock();
            RelationshipRootTestEntity relationshipRootTestEntityToUpdate = prepareToUpdateRelationshipRootTestEntity();
            relationshipRootTestEntityToUpdate.setId(oldRelationShipEntity.getId());
            relationshipRootTestEntityToUpdate.getSingleDependentTestEntity()
                    .setId(oldRelationShipEntity.getSingleDependentTestEntity().getId());
            relationshipRootTestEntityToUpdate.getDependentTestEntityList().get(0)
                    .setId(oldRelationShipEntity.getDependentTestEntityList().get(0).getId());
            relationshipRootTestEntityToUpdate.getDependentTestEntityList().get(1)
                    .setId(oldRelationShipEntity.getDependentTestEntityList().get(1).getId());
            s.merge(relationshipRootTestEntityToUpdate);
        };
        testEntityDao.executeConsumer(consumer);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void updateRelationshipEntityConsumer_transactionFailure() {
        Consumer<Session> relationshipRootTestEntitySupplier = (Session s) -> {
            RelationshipRootTestEntity relationshipRootTestEntity = prepareToSaveRelationshipRootTestEntity();
            relationshipRootTestEntity.setId(1L);
            s.merge(relationshipRootTestEntity);
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.executeConsumer(relationshipRootTestEntitySupplier);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/updateRelationshipTestEntityDataSet.yml")
    void findEntityAndUpdateEntity_success() {
        Parameter parameter = new Parameter("id", 1);
        RelationshipRootTestEntity relationshipRootTestEntity = prepareToUpdateRelationshipRootTestEntity();
        testEntityDao.findEntityAndUpdate(relationshipRootTestEntity, parameter);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void findEntityAndDeleteEntity_success() {
        Parameter parameter = new Parameter("id", 1);

        testEntityDao.findEntityAndDelete(parameter);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityBySupplier_success() {
        Supplier<RelationshipRootTestEntity> supplier = this::prepareRelationshipRootTestEntityDbMock;
        testEntityDao.deleteEntity(supplier);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityBySupplier_transactionFailure() {
        Supplier<RelationshipRootTestEntity> supplier = () -> {
            throw new RuntimeException();
        };
        Exception exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.deleteEntity(supplier);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByConsumer_success() {
        Consumer<Session> consumer = (Session s) -> {
            RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();
            s.remove(relationshipRootTestEntity.getSingleDependentTestEntity());
            relationshipRootTestEntity.getDependentTestEntityList().forEach(s::remove);
            s.remove(relationshipRootTestEntity);
        };

        testEntityDao.executeConsumer(consumer);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByConsumer_transactionFailure() {
        Consumer<Session> consumer = (Session s) -> {
            throw new RuntimeException();
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.executeConsumer(consumer);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByGeneralEntity_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        testEntityDao.deleteEntity(relationshipRootTestEntity);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByGeneralEntity_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.deleteEntity(relationshipRootTestEntity);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        Parameter parameter = new Parameter("id", 1L);

        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testEntityDao.findEntityAndDelete(parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityWithDependencies_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        RelationshipRootTestEntity result =
                testEntityDao.getEntity(parameter);

        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());
    }

    @Test
    void getEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testEntityDao.getEntity(parameter);
        });
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getOptionalEntityWithDependencies_success() {
        Parameter parameter = new Parameter("id", 1L);

        Optional<RelationshipRootTestEntity> result =
                testEntityDao.getOptionalEntity(parameter);

        assertTrue(result.isPresent());
        RelationshipRootTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Relationship Root Entity", resultEntity.getName());

    }

    @Test
    void getOptionalEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testEntityDao.getOptionalEntity(parameter);
        });

    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityList_success() {
        Parameter parameter = new Parameter("id", 1L);

        List<RelationshipRootTestEntity> result = testEntityDao.getEntityList(parameter);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
//        assertEquals(1, result.get(0).getSingleDependentTestEntity().getId());
//        assertEquals("Single Dependent Entity", result.get(0).getSingleDependentTestEntity().getName());
//        assertEquals("Dependent Entity", result.get(0).getDependentTestEntityList().get(0).getName());
//        assertEquals(1, result.get(0).getDependentTestEntityList().get(0).getId());
//        assertEquals("Dependent Entity", result.get(0).getDependentTestEntityList().get(1).getName());
//        assertEquals(2, result.get(0).getDependentTestEntityList().get(1).getId());
    }

    @Test
    void getEntityList_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testEntityDao.getEntityList(parameter);
        });
    }

//    private static class TestSingleEntityDaoExposed extends AbstractEntityChecker {
//        public TestSingleEntityDaoExposed() {
//            super(SingleTestEntity.class);
//        }
//
//        @Override
//        public <E> void classTypeChecker(E entity) {
//            super.classTypeChecker(entity);
//        }
//    }
//
//    @Test
//    void classTypeChecker_withMatchingTypes_shouldNotThrowException() {
//        SingleTestEntity singleTestEntity = new SingleTestEntity();
//        TestEntityDaoTest.TestSingleEntityDaoExposed singleEntityDao = new TestEntityDaoTest.TestSingleEntityDaoExposed();
//
//        assertDoesNotThrow(
//                () -> singleEntityDao.classTypeChecker(singleTestEntity));
//    }
//
//    @Test
//    void classTypeChecker_withNonMatchingTypes_shouldThrowException() {
//        Object object = new Object();
//        TestEntityDaoTest.TestSingleEntityDaoExposed singleEntityDao
//                = new TestEntityDaoTest.TestSingleEntityDaoExposed();
//
//        assertThrows(RuntimeException.class, () ->
//                singleEntityDao.classTypeChecker(object)
//        );
//    }
}
