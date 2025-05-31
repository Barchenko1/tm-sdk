package com.tm.core.test.manager.generic.entityManager;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.process.dao.generic.IGenericTransactionDao;
import com.tm.core.process.dao.generic.entityManager.AbstractGenericEntityManagerDao;
import com.tm.core.process.dao.generic.entityManager.AbstractGenericTransactionEntityManagerDao;
import com.tm.core.process.dao.generic.entityManager.GenericTransactionEntityManagerDao;
import com.tm.core.process.dao.transaction.EntityManagerTransactionHandler;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.manager.generic.IGenericTransactionOperationManager;
import com.tm.core.process.manager.generic.operator.AbstractGenericTransactionOperationManager;
import com.tm.core.process.manager.generic.operator.GenericTransactionOperationManager;
import com.tm.core.test.dao.AbstractDaoTest;
import jakarta.persistence.EntityManager;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericTransactionEntityManagerOperationManagerTest extends AbstractDaoTest {

    private final String GRAPH_PATH = "Employee.full";
    private final String NAMED_QUERY_NAME_ONE = "Employee.findByIdWithJoins";
    private final String ENTITY_PACKAGE = "com.tm.core.modal.relationship";
    private IGenericTransactionDao genericTransactionDao;
    private IGenericTransactionOperationManager genericTransactionOperationManager;

    @BeforeEach
    public void setupAll() {
        genericTransactionDao = new GenericTransactionEntityManagerDao(entityManager, ENTITY_PACKAGE);
        genericTransactionOperationManager = new GenericTransactionOperationManager(genericTransactionDao);
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

        genericTransactionOperationManager.persistEntity(employee);
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntityTypeCheck_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Object object = new Object();
        assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.persistEntity(object);
        });
    }

    @Test
    void persistEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToSaveRelationshipRootTestEntity();

        genericTransactionOperationManager.persistEntity(employee);
        verifyExpectedData("/datasets/relationship/saveMultipleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("New RelationshipRootTestEntity");

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).persist(employee);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.persistEntity(employee);
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

        genericTransactionOperationManager.persistSupplier(supplier);
        verifyExpectedData("/datasets/relationship/saveSingleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntitySupplier_transactionFailure() {
        loadDataSet("/datasets/relationship/emptyRelationshipTestEntityDataSet.yml");

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).persist(any(Employee.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.persistSupplier(supplier);
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

        genericTransactionOperationManager.executeConsumer(consumer);
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
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void updateEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToUpdateRelationshipRootTestEntity();
        genericTransactionOperationManager.updateEntity(employee);
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(100L);
        employee.setName("Update Entity");

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).merge(employee);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        assertThrows(RuntimeException.class, () -> genericTransactionOperationManager.updateEntity(employee));
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
        genericTransactionOperationManager.mergeSupplier(supplier);
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateRelationshipEntity_transactionFailure() {
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).merge(any(Employee.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();


        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.mergeSupplier(supplier);
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
        genericTransactionOperationManager.executeConsumer(consumer);
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
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void findEntityAndDeleteEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1);

        genericTransactionOperationManager.deleteEntityByParameter(Employee.class, parameter);
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityBySupplier_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;
        genericTransactionOperationManager.deleteSupplier(supplier);
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityBySupplier_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).remove(any(Employee.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.deleteSupplier(supplier);
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

        genericTransactionOperationManager.executeConsumer(consumer);
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityByConsumer_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Consumer<EntityManager> consumer = (EntityManager em) -> {
            throw new RuntimeException();
        };

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();


        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareRelationshipRootTestEntityDbMock();

        genericTransactionOperationManager.deleteEntity(employee);
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionEntityManagerDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).remove(any(Object.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Employee employee = prepareRelationshipRootTestEntityDbMock();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.deleteEntity(employee);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntity_transactionFailure() {
        Employee employee = prepareRelationshipRootTestEntityDbMock();

        Parameter parameter = new Parameter("id", 1L);

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        Query<Employee> query = mock(Query.class);

        try {
            Field transactionHandlerField = AbstractGenericEntityManagerDao.class.getDeclaredField("entityManager");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, entityManager);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericTransactionOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.createNamedQuery(anyString(), eq(Employee.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(employee);
        doThrow(new RuntimeException()).when(entityManager).remove(any(Object.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.deleteEntityByParameter(Employee.class, parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void getEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Employee result = genericTransactionOperationManager.getGraphEntityClose(Employee.class, GRAPH_PATH, parameter);

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
        Employee entity = genericTransactionOperationManager.getGraphEntityClose(Employee.class, GRAPH_PATH, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
    }

    @Test
    public void testGetEntityGraph_Failure() {
        assertThrows(RuntimeException.class, () -> {
            genericTransactionOperationManager.getGraphEntityClose(Employee.class, GRAPH_PATH, new Parameter("id", 1));
        });
    }

    @Test
    void getEntityWithDependencies() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee result =
                genericTransactionOperationManager.getNamedQueryEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, new Parameter("id", 1));

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
            genericTransactionOperationManager.getNamedQueryEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });
    }

    @Test
    void getOptionalEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional =
                genericTransactionOperationManager.getGraphOptionalEntityClose(Employee.class, GRAPH_PATH, parameter);

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
            genericTransactionOperationManager.getGraphOptionalEntityClose(Employee.class, GRAPH_PATH, parameter);
        });

    }

    @Test
    void getOptionalEntityNamedQueryWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional =
                genericTransactionOperationManager.getNamedQueryOptionalEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

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
            genericTransactionOperationManager.getNamedQueryOptionalEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });

    }

    @Test
    void getEntityListGraph_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = genericTransactionOperationManager.getGraphEntityListClose(Employee.class, GRAPH_PATH, parameter);

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

        assertThrows(RuntimeException.class, () -> genericTransactionOperationManager.getGraphEntityListClose(Employee.class, GRAPH_PATH, parameter));
    }

    @Test
    void getNamedQueryEntityList_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = genericTransactionOperationManager.getNamedQueryEntityListClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

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
            genericTransactionOperationManager.getNamedQueryEntityListClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });
    }

}