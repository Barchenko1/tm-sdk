package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.dsl.RiderDSL;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.basic.TestGeneralEntityDao;
import com.tm.core.dao.general.AbstractGeneralEntityDao;
import com.tm.core.dao.general.IGeneralEntityDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.GeneralEntity;
import com.tm.core.modal.relationship.DependentTestEntity;
import com.tm.core.modal.relationship.RelationshipRootTestEntity;
import com.tm.core.modal.relationship.SingleDependentTestEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.table.EntityTable;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
class GeneralEntityDaoTest {

    @Mock
    private static IEntityIdentifierDao entityIdentifierDao;

    @InjectMocks
    private static TestGeneralEntityDao testRelationshipEntityDao;

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
        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        testRelationshipEntityDao = new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
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
    void getEntityList_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        when(entityIdentifierDao.getEntityList(eq(RelationshipRootTestEntity.class), eq(parameter))).thenReturn(List.of(relationshipRootTestEntity));

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
    void getEntityListWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        when(entityIdentifierDao.getEntityList(eq(RelationshipRootTestEntity.class), eq(parameter))).thenReturn(List.of(relationshipRootTestEntity));

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
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntityList(RelationshipRootTestEntity.class, parameter);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntityList(parameter);
        });
    }

    @Test
    void getEntityListWithClass_Failure() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntityList(RelationshipRootTestEntity.class, parameter);

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
        relationshipRootTestEntity.setId(100L);
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        IGeneralEntityDao testRelationshipEntityDao =
                new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
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
    void saveRelationshipEntityConsumer_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        IGeneralEntityDao testRelationshipEntityDao =
                new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(transaction).commit();

        Consumer<Session> consumer = (Session s) -> {
            RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
            relationshipRootTestEntity.setId(100L);
            relationshipRootTestEntity.setName("New RelationshipRootTestEntity");
            s.persist(relationshipRootTestEntity);
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.saveGeneralEntity(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
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

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        IGeneralEntityDao testRelationshipEntityDao =
                new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(any());

        Supplier<RelationshipRootTestEntity> relationshipRootTestEntitySupplier = () -> {
            return null;
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.updateGeneralEntity(relationshipRootTestEntitySupplier);
        });
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
    void updateRelationshipEntityConsumer_transactionFailure() {
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        IGeneralEntityDao testRelationshipEntityDao =
                new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(any());

        Consumer<Session> relationshipRootTestEntitySupplier = (Session s) -> {
            RelationshipRootTestEntity relationshipRootTestEntity = prepareToSaveRelationshipRootTestEntity();
            relationshipRootTestEntity.setId(1L);
            s.merge(relationshipRootTestEntity);
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.updateGeneralEntity(relationshipRootTestEntitySupplier);
        });
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntity_success() {
        Parameter parameter = new Parameter("id", 1);
        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        when(entityIdentifierDao.getEntityList(RelationshipRootTestEntity.class, parameter))
                .thenReturn(List.of(relationshipRootTestEntity));

        testRelationshipEntityDao.deleteGeneralEntity(parameter);
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    @ExpectedDataSet("datasets/general/emptyRelationshipTestEntityDataSet.yml")
    void deleteRelationshipEntityWithClass_success() {
        Parameter parameter = new Parameter("id", 1);
        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();

        when(entityIdentifierDao.getEntityList(RelationshipRootTestEntity.class, parameter))
                .thenReturn(List.of(relationshipRootTestEntity));

        testRelationshipEntityDao.deleteGeneralEntity(RelationshipRootTestEntity.class, parameter);
    }

    @Test
    void deleteRelationshipEntity_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        Parameter parameter = new Parameter("id", 100L);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        IGeneralEntityDao testRelationshipEntityDao =
                new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(entityIdentifierDao.getEntityList(any(), eq(parameter))).thenReturn(List.of(relationshipRootTestEntity));
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.deleteGeneralEntity(parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityWithClass_transactionFailure() {
        RelationshipRootTestEntity relationshipRootTestEntity = new RelationshipRootTestEntity();
        relationshipRootTestEntity.setName("New RelationshipRootTestEntity");

        Parameter parameter = new Parameter("id", 100L);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);

        IGeneralEntityDao testRelationshipEntityDao =
                new TestGeneralEntityDao(sessionFactory, entityIdentifierDao);

        try {
            Field sessionManagerField = AbstractGeneralEntityDao.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(testRelationshipEntityDao, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(entityIdentifierDao.getEntityList(any(), eq(parameter))).thenReturn(List.of(relationshipRootTestEntity));
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(2, relationshipRootTestEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.deleteGeneralEntity(RelationshipRootTestEntity.class, parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void getOptionalEntityWithDependencies_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();
        when(entityIdentifierDao.getOptionalEntity(eq(RelationshipRootTestEntity.class), eq(parameter))).thenReturn(Optional.of(relationshipRootTestEntity));

        Optional<RelationshipRootTestEntity> result =
                testRelationshipEntityDao.getOptionalGeneralEntity(parameter);

        assertTrue(result.isPresent());
        RelationshipRootTestEntity resultEntity = result.get();
        assertEquals(1L, resultEntity.getId());
        assertEquals("Relationship Root Entity", resultEntity.getName());
    }

    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getOptionalEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getOptionalEntity(RelationshipRootTestEntity.class, parameter);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getOptionalGeneralEntity(parameter);
        });
    }

    @Test
    void getOptionalEntityWithDependenciesWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();
        when(entityIdentifierDao.getOptionalEntity(eq(RelationshipRootTestEntity.class), eq(parameter))).thenReturn(Optional.of(relationshipRootTestEntity));

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
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getOptionalEntity(RelationshipRootTestEntity.class, parameter);

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
        when(entityIdentifierDao.getEntity(eq(RelationshipRootTestEntity.class), eq(parameter))).thenReturn(relationshipRootTestEntity);

        RelationshipRootTestEntity result =
                testRelationshipEntityDao.getGeneralEntity(parameter);

        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());
    }

    @Test
    void getEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntity(RelationshipRootTestEntity.class, parameter);

        assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntity(parameter);
        });
    }


    @Test
    @DataSet("datasets/general/testRelationshipTestEntityDataSet.yml")
    void getEntityWithDependenciesWithClass_success() {
        Parameter parameter = new Parameter("id", 1L);

        RelationshipRootTestEntity relationshipRootTestEntity = prepareRelationshipRootTestEntityDbMock();
        when(entityIdentifierDao.getEntity(eq(RelationshipRootTestEntity.class), eq(parameter))).thenReturn(relationshipRootTestEntity);

        RelationshipRootTestEntity result =
                testRelationshipEntityDao.getGeneralEntity(RelationshipRootTestEntity.class, parameter);

        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());
    }

    @Test
    void getEntityWithDependenciesWithClass_Failure() {
        Parameter parameter = new Parameter("id", 100L);

        doThrow(new RuntimeException()).when(entityIdentifierDao).getEntity(RelationshipRootTestEntity.class, parameter);

        assertThrows(RuntimeException.class, () -> {
            testRelationshipEntityDao.getGeneralEntity(RelationshipRootTestEntity.class, parameter);
        });
    }


}
