package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.basic.TestGeneralEntityDao;
import com.tm.core.dao.general.AbstractGeneralEntityDao;
import com.tm.core.dao.general.IGeneralEntityDao;
import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.single.AbstractSingleEntityDao;
import com.tm.core.modal.GeneralEntity;
import com.tm.core.modal.relationship.DependentTestEntity;
import com.tm.core.modal.relationship.RelationshipRootTestEntity;
import com.tm.core.modal.relationship.SingleDependentTestEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
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
class GeneralEntityDaoTest {

    private static IThreadLocalSessionManager sessionManager;
    private static SessionFactory sessionFactory;

    private static TestGeneralEntityDao testRelationshipEntityDao;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        sessionFactory = getSessionFactory();
        sessionManager = new ThreadLocalSessionManager(sessionFactory);

        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        IEntityIdentifierDao entityIdentifierDao = new EntityIdentifierDao(sessionManager, entityMappingManager);
        testRelationshipEntityDao = new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionFactoryField = AbstractGeneralEntityDao.class.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(testRelationshipEntityDao, sessionFactory);

            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, sessionManager);
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
        singleDependentTestEntity.setName("Updated Single Dependent Entity");

        DependentTestEntity dependentTestEntity1 = new DependentTestEntity();
        dependentTestEntity1.setName("Updated Dependent Entity");
        DependentTestEntity dependentTestEntity2 = new DependentTestEntity();
        dependentTestEntity2.setName("Updated Dependent Entity");

        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
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

    private GeneralEntity prepareRelationshipRootTestEntity(RelationshipRootTestEntity relationshipRootTestEntity) {
        GeneralEntity generalEntity = new GeneralEntity();

        generalEntity.addEntityPriority(1, relationshipRootTestEntity.getSingleDependentTestEntity());
        generalEntity.addEntityPriority(1, relationshipRootTestEntity.getDependentTestEntityList());
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        return generalEntity;
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityList_success() {
        Parameter parameter = new Parameter("id", 1L);

        List<RelationshipRootTestEntity> result =
                testRelationshipEntityDao.getGeneralEntityList(parameter);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
        assertEquals(1, result.get(0).getSingleDependentTestEntity().getId());
        assertEquals("Single Dependent Entity", result.get(0).getSingleDependentTestEntity().getName());
        assertEquals("Dependent Entity", result.get(0).getDependentTestEntityList().get(0).getName());
        assertEquals(1, result.get(0).getDependentTestEntityList().get(0).getId());
        assertEquals("Dependent Entity", result.get(0).getDependentTestEntityList().get(1).getName());
        assertEquals(2, result.get(0).getDependentTestEntityList().get(1).getId());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityListWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        List<RelationshipRootTestEntity> result =
                testRelationshipEntityDao.getGeneralEntityList(RelationshipRootTestEntity.class, parameter);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
        assertEquals(1, result.get(0).getSingleDependentTestEntity().getId());
        assertEquals("Single Dependent Entity", result.get(0).getSingleDependentTestEntity().getName());
        assertEquals("Dependent Entity", result.get(0).getDependentTestEntityList().get(0).getName());
        assertEquals(1, result.get(0).getDependentTestEntityList().get(0).getId());
        assertEquals("Dependent Entity", result.get(0).getDependentTestEntityList().get(1).getName());
        assertEquals(2, result.get(0).getDependentTestEntityList().get(1).getId());
    }

    @Test
    void getEntityList_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntityList(parameter);
        });
    }

    @Test
    void getEntityListWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntityList(Object.class, parameter);
        });
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/saveSingleRelationshipTestEntityDataSet.yml")
    void saveRelationshipEntity_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("Relationship Root Entity");

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(1, relationshipRootTestEntity);

        testRelationshipEntityDao.saveGeneralEntity(generalEntity);
    }

    @Test
    @DataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/saveMultipleRelationshipTestEntityDataSet.yml")
    void saveEntityWithDependencies_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareToSaveRelationshipRootTestEntity();
        GeneralEntity generalEntity = prepareRelationshipRootTestEntity(relationshipRootTestEntity);

        testRelationshipEntityDao.saveGeneralEntity(generalEntity);
    }

    @Test
    void saveRelationshipEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setId(1L);
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(relationshipRootTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.saveGeneralEntity(generalEntity);
        });

        assertEquals(RuntimeException.class, exception.getClass());
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

        testRelationshipEntityDao.saveGeneralEntity(consumer);
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
            testRelationshipEntityDao.saveGeneralEntity(consumer);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
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
        testRelationshipEntityDao.updateGeneralEntity(relationshipEntitySupplier);
    }

    @Test
    void updateRelationshipEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareToSaveRelationshipRootTestEntity();
        relationshipRootTestEntity.setId(1L);

        Supplier<RelationshipRootTestEntity> relationshipRootTestEntitySupplier = () -> {
            throw new RuntimeException();
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.updateGeneralEntity(relationshipRootTestEntitySupplier);
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
        testRelationshipEntityDao.updateGeneralEntity(consumer);
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
            testRelationshipEntityDao.updateGeneralEntity(relationshipRootTestEntitySupplier);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntity_success() {
        Parameter parameter = new Parameter("id", 1);

        testRelationshipEntityDao.deleteGeneralEntity(parameter);
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

        testRelationshipEntityDao.deleteGeneralEntity(consumer);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByConsumer_transactionFailure() {
        Consumer<Session> consumer = (Session s) -> {
            throw new RuntimeException();
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.deleteGeneralEntity(consumer);
        });

        assertEquals(IllegalStateException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByGeneralEntity_success() {
        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(1, relationshipRootTestEntity.getDependentTestEntityList().get(0));
        generalEntity.addEntityPriority(1, relationshipRootTestEntity.getDependentTestEntityList().get(1));
        generalEntity.addEntityPriority(1, relationshipRootTestEntity.getSingleDependentTestEntity());
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        testRelationshipEntityDao.deleteGeneralEntity(generalEntity);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityByGeneralEntity_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("sessionFactory");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, sessionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.deleteGeneralEntity(generalEntity);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 1);

        testRelationshipEntityDao.deleteGeneralEntity(RelationshipRootTestEntity.class, parameter);
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
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.deleteGeneralEntity(parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityWithClass_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        Parameter parameter = new Parameter("id", 1L);

        IThreadLocalSessionManager sessionManager = mock(IThreadLocalSessionManager.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, sessionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionManager.getSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.deleteGeneralEntity(RelationshipRootTestEntity.class, parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getOptionalEntityWithDependencies_success() {
        Parameter parameter = new Parameter("id", 1L);

        Optional<RelationshipRootTestEntity> result =
                testRelationshipEntityDao.getOptionalGeneralEntity(parameter);

        assertTrue(result.isPresent());
        RelationshipRootTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Relationship Root Entity", resultEntity.getName());

    }

    @Test
    void getOptionalEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getOptionalGeneralEntity(parameter);
        });

    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getOptionalEntityWithDependenciesWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        Optional<RelationshipRootTestEntity> result =
                testRelationshipEntityDao.getOptionalGeneralEntity(RelationshipRootTestEntity.class, parameter);

        assertTrue(result.isPresent());
        RelationshipRootTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Relationship Root Entity", resultEntity.getName());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getOptionalEntityWithDependenciesWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getOptionalGeneralEntity(RelationshipRootTestEntity.class, parameter);
        });
    }

    @Test
    void getOptionalEntityWithDependencies_OptionEmpty() {
        Parameter parameter = new Parameter("id", 100L);

        Optional<RelationshipRootTestEntity> result =
                testRelationshipEntityDao.getOptionalGeneralEntity(RelationshipRootTestEntity.class, parameter);

        assertTrue(result.isEmpty());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityWithDependencies_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        RelationshipRootTestEntity result =
                testRelationshipEntityDao.getGeneralEntity(parameter);

        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());
    }

    @Test
    void getEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntity(parameter);
        });
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityWithDependenciesWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        RelationshipRootTestEntity result =
                testRelationshipEntityDao.getGeneralEntity(RelationshipRootTestEntity.class, parameter);

        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());
    }

    @Test
    void getEntityWithDependenciesWithClass_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntity(RelationshipRootTestEntity.class, parameter);
        });
    }


}
