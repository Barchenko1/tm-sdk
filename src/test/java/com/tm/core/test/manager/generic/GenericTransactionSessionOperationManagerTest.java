package com.tm.core.test.manager.generic;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.process.dao.generic.IGenericTransactionDao;
import com.tm.core.process.dao.generic.session.AbstractGenericSessionDao;
import com.tm.core.process.dao.generic.session.AbstractGenericTransactionSessionDao;
import com.tm.core.process.dao.generic.session.GenericTransactionSessionDao;
import com.tm.core.process.dao.transaction.EntityManagerTransactionHandler;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.dao.transaction.SessionTransactionHandler;
import com.tm.core.process.manager.generic.AbstractGenericTransactionOperationManager;
import com.tm.core.process.manager.generic.GenericTransactionOperationManager;
import com.tm.core.process.manager.generic.IGenericOperationManager;
import com.tm.core.test.dao.AbstractDaoTest;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

class GenericTransactionSessionOperationManagerTest extends AbstractDaoTest {

    private final String GRAPH_PATH = "Employee.full";
    private final String NAMED_QUERY_NAME_ONE = "Employee.findByIdWithJoins";
    private final String ENTITY_PACKAGE = "com.tm.core.modal.relationship";
    private IGenericTransactionDao genericTransactionDao;
    private IGenericOperationManager genericOperationManager;

    @BeforeEach
    public void setupAll() {
        genericTransactionDao = new GenericTransactionSessionDao(sessionFactory, ENTITY_PACKAGE);
        genericOperationManager = new GenericTransactionOperationManager(genericTransactionDao);
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

        genericOperationManager.persistEntity(employee);
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

        genericOperationManager.persistEntity(employee);
        verifyExpectedData("/datasets/relationship/saveMultipleRelationshipTestEntityDataSet.yml");
    }

    @Test
    void saveRelationshipEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("New RelationshipRootTestEntity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new SessionTransactionHandler(sessionFactory);

        try {
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).persist(employee);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.persistEntity(employee);
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

        genericOperationManager.persistSupplier(supplier);
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
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).persist(any(Employee.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.persistSupplier(supplier);
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

        genericOperationManager.executeConsumer(consumer);
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
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void updateEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareToUpdateRelationshipRootTestEntity();
        genericOperationManager.updateEntity(employee);
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateEntity_transactionFailure() {
        Employee employee = new Employee();
        employee.setId(100L);
        employee.setName("Update Entity");

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new SessionTransactionHandler(sessionFactory);

        try {
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).merge(employee);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        assertThrows(RuntimeException.class, () -> genericOperationManager.updateEntity(employee));
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
        genericOperationManager.mergeSupplier(supplier);
        verifyExpectedData("/datasets/relationship/updateRelationshipTestEntityDataSet.yml");
    }

    @Test
    void updateRelationshipEntity_transactionFailure() {
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;

        EntityManager entityManager = mock(EntityManager.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new EntityManagerTransactionHandler(entityManager);

        try {
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).merge(any(Employee.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();


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
        genericOperationManager.executeConsumer(consumer);
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
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void findEntityAndDeleteEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1);

        genericOperationManager.deleteEntityByParameter(Employee.class, parameter);
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityBySupplier_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Supplier<Employee> supplier = this::prepareRelationshipRootTestEntityDbMock;
        genericOperationManager.deleteSupplier(supplier);
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
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(entityManager).remove(any(Employee.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.deleteSupplier(supplier);
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

        genericOperationManager.executeConsumer(consumer);
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
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();


        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.executeConsumer(consumer);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee employee = prepareRelationshipRootTestEntityDbMock();

        genericOperationManager.deleteEntity(employee);
        verifyExpectedData("/datasets/relationship/deleteRelationshipTestEntityDataSet.yml");
    }

    @Test
    void deleteRelationshipEntityByGeneralEntity_transactionFailure() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        ITransactionHandler transactionHandler = new SessionTransactionHandler(sessionFactory);

        try {
            Field transactionHandlerField = AbstractGenericTransactionSessionDao.class.getDeclaredField("transactionHandler");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, transactionHandler);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));
        when(transaction.isActive()).thenReturn(true);
        doNothing().when(transaction).rollback();

        Employee employee = prepareRelationshipRootTestEntityDbMock();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.deleteEntity(employee);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void deleteRelationshipEntity_transactionFailure() {
        Employee employee = prepareRelationshipRootTestEntityDbMock();

        Parameter parameter = new Parameter("id", 1L);

        SessionFactory sessionFactory = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        Query<Employee> query = mock(Query.class);

        try {
            Field transactionHandlerField = AbstractGenericSessionDao.class.getDeclaredField("sessionFactory");
            transactionHandlerField.setAccessible(true);
            transactionHandlerField.set(genericTransactionDao, sessionFactory);
            Field genericTransactionOperationManagerField = AbstractGenericTransactionOperationManager.class.getDeclaredField("genericTransactionDao");
            genericTransactionOperationManagerField.setAccessible(true);
            genericTransactionOperationManagerField.set(genericOperationManager, genericTransactionDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createNamedQuery(anyString(), eq(Employee.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(employee);
        doThrow(new RuntimeException()).when(session).remove(any(Object.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            genericOperationManager.deleteEntityByParameter(Employee.class, parameter);
        });

        assertEquals(RuntimeException.class, exception.getClass());
    }

    @Test
    void getEntityWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Employee result = genericOperationManager.getGraphEntityClose(Employee.class, GRAPH_PATH, parameter);

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
        Employee entity = genericOperationManager.getGraphEntityClose(Employee.class, GRAPH_PATH, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
    }

    @Test
    public void testGetEntityGraph_Failure() {
        assertThrows(RuntimeException.class, () -> {
            genericOperationManager.getGraphEntityClose(Employee.class, GRAPH_PATH, new Parameter("id", 1));
        });
    }

    @Test
    void getEntityWithDependencies() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Employee result =
                genericOperationManager.getNamedQueryEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, new Parameter("id", 1));

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
                genericOperationManager.getGraphOptionalEntityClose(Employee.class, GRAPH_PATH, parameter);

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
            genericOperationManager.getGraphOptionalEntityClose(Employee.class, GRAPH_PATH, parameter);
        });

    }

    @Test
    void getOptionalEntityNamedQueryWithDependencies_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Employee> optional =
                genericOperationManager.getNamedQueryOptionalEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

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
            genericOperationManager.getNamedQueryOptionalEntityClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        });

    }

    @Test
    void getEntityListGraph_success() {
        loadDataSet("/datasets/relationship/testRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Employee> result = genericOperationManager.getGraphEntityListClose(Employee.class, GRAPH_PATH, parameter);

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

        List<Employee> result = genericOperationManager.getNamedQueryEntityListClose(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

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