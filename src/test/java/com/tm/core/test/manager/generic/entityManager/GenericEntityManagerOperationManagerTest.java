package com.tm.core.test.manager.generic.entityManager;

import com.tm.core.configuration.TestJpaConfig;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.process.dao.generic.IGenericDao;
import com.tm.core.process.dao.generic.entityManager.AbstractGenericEntityManagerDao;
import com.tm.core.process.dao.generic.entityManager.GenericEntityManagerDao;
import com.tm.core.process.manager.generic.operator.AbstractGenericOperationManager;
import com.tm.core.process.manager.generic.operator.GenericOperationManager;
import com.tm.core.process.manager.generic.IGenericOperationManager;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
class GenericEntityManagerOperationManagerTest extends AbstractDaoTest {

    private final String GRAPH_PATH = "Employee.full";
    private final String NAMED_QUERY_NAME_ONE = "Employee.findByIdWithJoins";
    private final String ENTITY_PACKAGE = "com.tm.core.modal.relationship";
    private IGenericDao genericDao;
    private IGenericOperationManager genericOperationManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setupAll() {
        genericDao = new GenericEntityManagerDao(entityManager, ENTITY_PACKAGE);
        genericOperationManager = new GenericOperationManager(genericDao);
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

        transactionTemplate.executeWithoutResult(status -> {
            genericOperationManager.persistEntity(employee);
        });
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntityTypeCheck_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Object object = new Object();
        assertThrows(RuntimeException.class, () -> {
            genericOperationManager.persistEntity(object);
        });
    }

    @Test
    void persistEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToSaveRelationshipRootTestEntity();

        transactionTemplate.executeWithoutResult(status ->
                genericOperationManager.persistEntity(employee));
        verifyExpectedData("/datasets/relationship/saveMultipleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("New RelationshipRootTestEntity");

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).persist(employee);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status -> 
                    genericOperationManager.persistEntity(employee));
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

        transactionTemplate.executeWithoutResult(status ->
                genericOperationManager.persistSupplier(supplier));
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntitySupplier_transactionFailure() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).persist(any(Employee.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    genericOperationManager.persistSupplier(supplier));
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

        transactionTemplate.executeWithoutResult(status -> {
            genericOperationManager.executeConsumer(consumer);
        });
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
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    genericOperationManager.executeConsumer(consumer));
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void updateEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToUpdateRelationshipRootTestEntity();

        transactionTemplate.executeWithoutResult(status ->
                genericOperationManager.updateEntity(employee));

        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(100L);
        employee.setName("Update Entity");

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).merge(employee);

        assertThrows(RuntimeException.class, () -> 
                transactionTemplate.executeWithoutResult(status ->
                        genericOperationManager.updateEntity(employee)));
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

        transactionTemplate.executeWithoutResult(status ->
                genericOperationManager.mergeSupplier(supplier));
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateRelationshipEntity_transactionFailure() {
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).merge(any(Employee.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.mergeSupplier(supplier);
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

        transactionTemplate.executeWithoutResult(stratus ->
                genericOperationManager.executeConsumer(consumer)
        );
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
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    genericOperationManager.executeConsumer(consumer));
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityBySupplier_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        transactionTemplate.executeWithoutResult(status ->
                genericOperationManager.deleteSupplier(supplier));
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityBySupplier_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).remove(any(Employee.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status ->
                    genericOperationManager.deleteSupplier(supplier));
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

        transactionTemplate.executeWithoutResult(status -> {
            genericOperationManager.executeConsumer(consumer);
        });
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
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareRelationshipRootTestEntityDbMock();


        transactionTemplate.executeWithoutResult(status ->
                genericOperationManager.deleteEntity(employee));
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        EntityManager entityManager = mock(EntityManager.class);

        try {
            Field emField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(genericDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericOperationManager.class.getDeclaredField("genericDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException()).when(entityManager).remove(any(Object.class));

        Employee employee = prepareRelationshipRootTestEntityDbMock();

        Exception exception = assertThrows(RuntimeException.class, () ->
                transactionTemplate.executeWithoutResult(status ->
                        genericOperationManager.deleteEntity(employee)));

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void getEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Employee result = genericOperationManager.getGraphEntity(Employee.class, GRAPH_PATH, parameter);

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
    public void testGetEntityGraph() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee entity = genericOperationManager.getGraphEntity(Employee.class, GRAPH_PATH, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
    }

    @Test
    public void testGetEntityGraph_Failure() {
        assertThrows(RuntimeException.class, () -> {
            genericOperationManager.getGraphEntity(Employee.class, GRAPH_PATH, new Parameter("id", 1));
        });
    }

    @Test
    void getEntityWithDependencies() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        Employee result =
                genericOperationManager.getNamedQueryEntity(Employee.class, NAMED_QUERY_NAME_ONE, new Parameter("id", 1));

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
            genericOperationManager.getNamedQueryEntity(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });
    }

    @Test
    void getOptionalEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional =
                genericOperationManager.getGraphOptionalEntity(Employee.class, GRAPH_PATH, parameter);

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
            genericOperationManager.getGraphOptionalEntity(Employee.class, GRAPH_PATH, parameter);
        });

    }

    @Test
    void getOptionalEntityNamedQueryWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional =
                genericOperationManager.getNamedQueryOptionalEntity(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

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
            genericOperationManager.getNamedQueryOptionalEntity(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });

    }

    @Test
    void getEntityListGraph_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = genericOperationManager.getGraphEntityList(Employee.class, GRAPH_PATH, parameter);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
        assertEquals(1, result.get(0).getSpouse().getId());
        assertEquals("Dependent Entity", result.get(0).getSpouse().getName());

        List<Dependent> dependents = result.get(0).getDependentList();
        dependents.sort(Comparator.comparing(Dependent::getId));
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

        assertThrows(RuntimeException.class, () -> genericOperationManager.getGraphEntityList(Employee.class, GRAPH_PATH, parameter));
    }

    @Test
    void getNamedQueryEntityList_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = genericOperationManager.getNamedQueryEntityList(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Relationship Root Entity", result.get(0).getName());
        assertEquals(1, result.get(0).getSpouse().getId());
        assertEquals("Dependent Entity", result.get(0).getSpouse().getName());

        List<Dependent> dependents = result.get(0).getDependentList();
        dependents.sort(Comparator.comparing(Dependent::getId));
        int[] expectedIds = {2, 3};

        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], dependents.get(i).getId());
            assertEquals("Dependent Entity", dependents.get(i).getName());
        }
        assertEquals(1, result.get(0).getItemSet().iterator().next().getId());
        assertEquals("Item Entity", result.get(0).getItemSet().iterator().next().getName());
    }

    @Test
    void getNamedQueryEntityList_transactionFailure() {
        Parameter parameter = new Parameter("id1", 1L);

        assertThrows(RuntimeException.class, () -> {
            genericOperationManager.getNamedQueryEntityList(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });
    }

}