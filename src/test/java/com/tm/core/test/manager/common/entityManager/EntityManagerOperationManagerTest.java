package com.tm.core.test.manager.common.entityManager;

import com.tm.core.configuration.TestJpaConfig;
import com.tm.core.dao.basic.TestEntityManagerDao;
import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.table.EntityTable;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.process.dao.AbstractEntityChecker;
import com.tm.core.process.dao.common.IEntityDao;
import com.tm.core.process.dao.common.entityManager.AbstractEntityManagerDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.process.dao.query.QueryService;
import com.tm.core.process.manager.common.IEntityOperationManager;
import com.tm.core.process.manager.common.impl.AbstractEntityOperationManager;
import com.tm.core.process.manager.common.impl.EntityOperationManager;
import com.tm.core.test.dao.AbstractDaoTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
class EntityManagerOperationManagerTest extends AbstractDaoTest {

    private final String GRAPH_PATH = "Employee.full";
    private final String NAMED_QUERY_NAME_ONE = "Employee.findByIdWithJoins";
    private IEntityDao entityDao;
    private IEntityOperationManager entityOperationManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setupAll() {
        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        IQueryService queryService = new QueryService(entityMappingManager);
        entityDao = new TestEntityManagerDao(entityManager, queryService);
        entityOperationManager = new EntityOperationManager(entityDao);
    }

    private static IEntityMappingManager getEntityMappingManager() {
        EntityTable dependentTestEntity = new EntityTable(Dependent.class, "dependent");
        EntityTable singleDependentTestEntity = new EntityTable(Item.class, "item");
        EntityTable relationshipRootTestEntity = new EntityTable(Employee.class, "Employee", "Employee.findByValue");

        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(dependentTestEntity);
        entityMappingManager.addEntityTable(singleDependentTestEntity);
        entityMappingManager.addEntityTable(relationshipRootTestEntity);
        return entityMappingManager;
    }

    private Employee prepareToSaveRelationshipRootTestEntity() {
        Item item = new Item();
        item.setName("Item Entity");

        Dependent dependent1 = new Dependent();
        dependent1.setName("Dependent Entity");
        Dependent dependent2 = new Dependent();
        dependent2.setName("Dependent Entity");
        Dependent dependent3 = new Dependent();
        dependent3.setName("Dependent Entity");

        Employee employee = new Employee();
        employee.setName("Relationship Root Entity");
        employee.setSpouse(dependent1);
        employee.addItem(item);
        employee.setDependentList(Arrays.asList(dependent2, dependent3));

        return employee;
    }

    private Employee prepareToUpdateRelationshipRootTestEntity() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Updated Item Entity");

        Dependent dependent1 = new Dependent();
        dependent1.setId(1L);
        dependent1.setName("Updated Dependent Entity");
        Dependent dependent2 = new Dependent();
        dependent2.setId(2L);
        dependent2.setName("Updated Dependent Entity");
        Dependent dependent3 = new Dependent();
        dependent3.setId(3L);
        dependent3.setName("Updated Dependent Entity");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Updated Relationship Root Entity");
        employee.setSpouse(dependent1);
        employee.addItem(item);
        employee.setDependentList(Arrays.asList(dependent2, dependent3));

        return employee;
    }

    private Employee prepareRelationshipRootTestEntityDbMock() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Single Dependent Entity");

        Dependent dependent1 = new Dependent();
        dependent1.setId(1L);
        dependent1.setName("Dependent Entity");
        Dependent dependent2 = new Dependent();
        dependent2.setId(2L);
        dependent2.setName("Dependent Entity");
        Dependent dependent3 = new Dependent();
        dependent3.setId(3L);
        dependent3.setName("Dependent Entity");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Relationship Root Entity");
        employee.setSpouse(dependent1);
        employee.addItem(item);
        employee.setDependentList(Arrays.asList(dependent2, dependent3));

        return employee;
    }

    @Test
    void saveRelationshipEntity_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Employee employee = new Employee();
        employee.setName("Relationship Root Entity");

        transactionTemplate.executeWithoutResult(status ->
                entityOperationManager.persistEntity(employee));
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntityTypeCheck_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Object object = new Object();
        assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.persistEntity(object));
        });
    }

    @Test
    void saveEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToSaveRelationshipRootTestEntity();

        transactionTemplate.executeWithoutResult(status ->
                entityOperationManager.persistEntity(employee));
        verifyExpectedData("/datasets/relationship/saveMultipleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("New RelationshipRootTestEntity");

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).persist(employee);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(transactionStatus ->
                    entityOperationManager.persistEntity(employee));
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void saveRelationshipEntitySupplier_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Supplier<Employee> supplier = () -> {
            Employee employee = new Employee();
            employee.setName("Relationship Root Entity");
            return employee;
        };

        transactionTemplate.executeWithoutResult(transactionStatus ->
                entityOperationManager.persistSupplier(supplier));
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntitySupplier_transactionFailure() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).persist(any(Employee.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.persistSupplier(supplier));
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void saveRelationshipEntityConsumer_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            Employee employee = new Employee();
            employee.setName("Relationship Root Entity");
            em.persist(employee);
        };

        transactionTemplate.executeWithoutResult(transactionStatus ->
                entityOperationManager.executeConsumer(consumer));
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntityConsumer_transactionFailure() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            Employee employee = new Employee();
            employee.setName("New RelationshipRootTestEntity");
            em.persist(employee);
            throw new RuntimeException();
        };

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void updateEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToUpdateRelationshipRootTestEntity();
        
        transactionTemplate.executeWithoutResult(transactionStatus ->
                entityOperationManager.mergeEntity(employee));
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(100L);
        employee.setName("Update Entity");

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).merge(employee);

        assertThrows(RuntimeException.class, () -> 
                transactionTemplate.executeWithoutResult(transactionStatus -> 
                        entityOperationManager.mergeEntity(employee)));
    }

    @Test
    void updateRelationshipEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Supplier<Employee> supplier = () -> {
            Employee oldRelationShipEntity = prepareRelationshipRootTestEntityDbMock();
            Employee employeeToUpdate = prepareToUpdateRelationshipRootTestEntity();
            employeeToUpdate.setId(oldRelationShipEntity.getId());
            return employeeToUpdate;
        };

        transactionTemplate.executeWithoutResult(transactionStatus ->
                entityOperationManager.mergeSupplier(supplier));
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateRelationshipEntity_transactionFailure() {
        Employee employee = prepareToSaveRelationshipRootTestEntity();
        employee.setId(1L);

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).merge(any(Employee.class));


        Exception exception = assertThrows(RuntimeException.class, () -> {
            entityOperationManager.mergeSupplier(supplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void updateRelationshipEntityConsumer_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            Employee oldRelationShipEntity = prepareRelationshipRootTestEntityDbMock();
            Employee employeeToUpdate = prepareToUpdateRelationshipRootTestEntity();
            employeeToUpdate.setId(oldRelationShipEntity.getId());
            em.merge(employeeToUpdate);
        };

        transactionTemplate.executeWithoutResult(status ->
                entityOperationManager.executeConsumer(consumer));
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateRelationshipEntityConsumer_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            Employee employee = prepareToSaveRelationshipRootTestEntity();
            employee.setId(1L);
            em.merge(employee);
            throw new RuntimeException();
        };

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityBySupplier_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        transactionTemplate.executeWithoutResult(transactionStatus ->
                entityOperationManager.deleteSupplier(supplier));
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityBySupplier_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).remove(any(Employee.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            entityOperationManager.deleteSupplier(supplier);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityByConsumer_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            Employee employee = prepareRelationshipRootTestEntityDbMock();
            em.remove(employee);
        };

        transactionTemplate.executeWithoutResult(status ->
                entityOperationManager.executeConsumer(consumer));
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityByConsumer_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            throw new RuntimeException();
        };

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.executeConsumer(consumer));
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareRelationshipRootTestEntityDbMock();

        transactionTemplate.executeWithoutResult(status ->
                entityOperationManager.deleteEntity(employee));
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field entityDaoField = AbstractEntityManagerDao.class.getDeclaredField("entityManager");
            entityDaoField.setAccessible(true);
            entityDaoField.set(entityDao, entityManager);
            Field entityOperationManagerField = AbstractEntityOperationManager.class.getDeclaredField("entityDao");
            entityOperationManagerField.setAccessible(true);
            entityOperationManagerField.set(entityOperationManager, entityDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).remove(any(Object.class));

        Employee employee = prepareRelationshipRootTestEntityDbMock();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.deleteEntity(employee));
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void getEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Employee result = entityOperationManager.getGraphEntity(GRAPH_PATH, parameter);

        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());

        assertEquals("Relationship Root Entity", result.getName());
        assertEquals(1, result.getSpouse().getId());
        assertEquals("Dependent Entity", result.getSpouse().getName());

        assertEquals(2, result.getDependentList().size());

        assertEquals(1, result.getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.getItemSet().iterator().next().getName());
    }

    @Test
    public void testGetEntityGraph_Failure() {
        assertThrows(RuntimeException.class, () -> {
            transactionTemplate.execute(status ->
                    entityOperationManager.getGraphEntity(GRAPH_PATH, new Parameter("id", 1)));
        });
    }

    @Test
    void getEntityWithDependencies() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee result = transactionTemplate.execute(status ->
                entityOperationManager.getNamedQueryEntity(NAMED_QUERY_NAME_ONE, new Parameter("id", 1)));

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());

        assertEquals("Relationship Root Entity", result.getName());
        assertEquals(1, result.getSpouse().getId());
        assertEquals("Dependent Entity", result.getSpouse().getName());

        List<Dependent> dependents = result.getDependentList();
        dependents.sort(Comparator.comparing(Dependent::getId));
        int[] expectedIds = {2, 3};

        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], dependents.get(i).getId());
            assertEquals("Dependent Entity", dependents.get(i).getName());
        }

        assertEquals(1, result.getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.getItemSet().iterator().next().getName());
    }

    @Test
    void getEntityWithDependencies_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.getNamedQueryEntity(NAMED_QUERY_NAME_ONE, parameter));
        });
    }

    @Test
    void getOptionalEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional = transactionTemplate.execute(status ->
                entityOperationManager.getGraphOptionalEntity(GRAPH_PATH, parameter));

        assertNotNull(optional);
        assertTrue(optional.isPresent());
        Employee result = optional.get();
        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());

        assertEquals("Relationship Root Entity", result.getName());
        assertEquals(1, result.getSpouse().getId());
        assertEquals("Dependent Entity", result.getSpouse().getName());

        List<Dependent> dependents = result.getDependentList();
        dependents.sort(Comparator.comparing(Dependent::getId));
        int[] expectedIds = {2, 3};

        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], dependents.get(i).getId());
            assertEquals("Dependent Entity", dependents.get(i).getName());
        }

        assertEquals(1, result.getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.getItemSet().iterator().next().getName());
    }

    @Test
    void getOptionalEntityGraph_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.getGraphOptionalEntity(GRAPH_PATH, parameter));
        });

    }

    @Test
    void getOptionalEntityNamedQueryWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional = transactionTemplate.execute(status ->
                entityOperationManager.getNamedQueryOptionalEntity(NAMED_QUERY_NAME_ONE, parameter));

        assertNotNull(optional);
        assertTrue(optional.isPresent());
        Employee result = optional.get();
        assertEquals(1L, result.getId());
        assertEquals("Relationship Root Entity", result.getName());

        assertEquals("Relationship Root Entity", result.getName());
        assertEquals(1, result.getSpouse().getId());
        assertEquals("Dependent Entity", result.getSpouse().getName());

        List<Dependent> dependents = result.getDependentList();
        dependents.sort(Comparator.comparing(Dependent::getId));
        int[] expectedIds = {2, 3};

        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], dependents.get(i).getId());
            assertEquals("Dependent Entity", dependents.get(i).getName());
        }

        assertEquals(1, result.getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.getItemSet().iterator().next().getName());
    }

    @Test
    void getOptionalEntityNamedQuery_Failure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            entityOperationManager.getNamedQueryOptionalEntity(NAMED_QUERY_NAME_ONE, parameter);
        });

    }

    @Test
    void getEntityListGraph_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = entityOperationManager.getGraphEntityList(GRAPH_PATH, parameter);
        result.sort(Comparator.comparingLong(Employee::getId));

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
        assertEquals(1, result.get(0).getSpouse().getId());
        assertEquals("Dependent Entity", result.get(0).getSpouse().getName());

        List<Dependent> dependents = result.get(0).getDependentList();
        dependents.sort(Comparator.comparingLong(Dependent::getId));
        int[] expectedIds = {2, 3};

        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], dependents.get(i).getId());
            assertEquals("Dependent Entity", dependents.get(i).getName());
        }

        assertEquals(1, result.get(0).getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.get(0).getItemSet().iterator().next().getName());
    }

    @Test
    void getEntityListGraph_transactionFailure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () ->
                transactionTemplate.executeWithoutResult(status ->
                        entityOperationManager.getGraphEntityList(GRAPH_PATH, parameter)));
    }

    @Test
    void getNamedQueryEntityLis_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = transactionTemplate.execute(status ->
                entityOperationManager.getNamedQueryEntityList(NAMED_QUERY_NAME_ONE, parameter));

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
        assertEquals(1, result.get(0).getSpouse().getId());
        assertEquals("Dependent Entity", result.get(0).getSpouse().getName());

        assertEquals(2, result.get(0).getDependentList().size());

        assertEquals(1, result.get(0).getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.get(0).getItemSet().iterator().next().getName());
    }

    @Test
    void getNamedQueryEntityList_transactionFailure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    entityOperationManager.getNamedQueryEntityList(NAMED_QUERY_NAME_ONE, parameter));
        });
    }

    private static class ItemDaoExposed extends AbstractEntityChecker {
        public ItemDaoExposed() {
            super(Item.class);
        }

        @Override
        public <E> void classTypeChecker(E entity) {
            super.classTypeChecker(entity);
        }
    }

    @Test
    void classTypeChecker_withMatchingTypes_shouldNotThrowException() {
        Item item = new Item();
        EntityManagerOperationManagerTest.ItemDaoExposed singleEntityDao = new EntityManagerOperationManagerTest.ItemDaoExposed();

        assertDoesNotThrow(
                () -> singleEntityDao.classTypeChecker(item));
    }

    @Test
    void classTypeChecker_withNonMatchingTypes_shouldThrowException() {
        Object object = new Object();
        EntityManagerOperationManagerTest.ItemDaoExposed singleEntityDao
                = new EntityManagerOperationManagerTest.ItemDaoExposed();

        assertThrows(RuntimeException.class, () ->
                singleEntityDao.classTypeChecker(object)
        );
    }
}