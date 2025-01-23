package com.tm.core.test.dao;

import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.table.EntityTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityIdentifierDaoTest extends AbstractDaoTest {

    private final String GRAPH_PATH = "Employee.full";
    private EntityIdentifierDao entityIdentifierDao;

    @BeforeEach
    public void setUpAll() {
        MockitoAnnotations.openMocks(this);
        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        entityIdentifierDao = new EntityIdentifierDao(entityMappingManager);
    }

    private static IEntityMappingManager getEntityMappingManager() {
        EntityTable dependentTestEntity = new EntityTable(Dependent.class, "dependent");
        EntityTable singleDependentTestEntity = new EntityTable(Item.class, "item");
        EntityTable relationshipRootTestEntity = new EntityTable(Employee.class, "Employee");

        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(dependentTestEntity);
        entityMappingManager.addEntityTable(singleDependentTestEntity);
        entityMappingManager.addEntityTable(relationshipRootTestEntity);
        return entityMappingManager;
    }

    @Test
    public void testGetEntityList() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");

        List<Item> entities =
                entityIdentifierDao.getEntityList(sessionFactory.openSession(), Item.class, new Parameter("id", 1));

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(1, entities.size());
    }

    @Test
    public void testGetEntityListGraph() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");

        Employee expectedEmployee = getEmployee();

        List<Employee> entities =
                entityIdentifierDao.getEntityListGraph(sessionFactory.openSession(), GRAPH_PATH, Employee.class, new Parameter("id", 1));

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(1, entities.size());
        checkEmployee(expectedEmployee, entities.get(0));
    }

    @Test
    public void testGetEntityListGraphAll() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");

        List<Employee> entities =
                entityIdentifierDao.getEntityListGraph(sessionFactory.openSession(), GRAPH_PATH, Employee.class);

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(2, entities.size());
    }

    @Test
    public void testGetEntity() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        Item entity =
                entityIdentifierDao.getEntity(sessionFactory.openSession(), Item.class, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
    }

    @Test
    public void testGetEntityGraph() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Employee expectedEmployee = getEmployee();
        Employee entity =
                entityIdentifierDao.getEntityGraph(sessionFactory.openSession(), GRAPH_PATH, Employee.class, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        checkEmployee(expectedEmployee, entity);
    }

    @Test
    public void testGetOptionalEntity() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        Optional<Item> optionalEntity =
                entityIdentifierDao.getOptionalEntity(sessionFactory.openSession(), Item.class, new Parameter("id", 1));

        assertTrue(optionalEntity.isPresent());
        assertEquals(1, optionalEntity.get().getId());
    }

    @Test
    public void testGetOptionalEntityGraph() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Optional<Employee> optionalEntity =
                entityIdentifierDao.getOptionalEntityGraph(sessionFactory.openSession(), GRAPH_PATH, Employee.class, new Parameter("id", 1));

        assertTrue(optionalEntity.isPresent());
        assertEquals(1, optionalEntity.get().getId());
        checkEmployee(getEmployee(), optionalEntity.get());
    }

    @Test
    public void testGetListEntity() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        List<Item> entities =
                entityIdentifierDao.getEntityList(sessionFactory.openSession(), Item.class, new Parameter("id", 1));

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(1, entities.size());
    }

    @Test
    public void testGetListEntityWithMultiOneTypeParams() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        List<Item> entities =
                entityIdentifierDao.getEntityList(sessionFactory.openSession(), Item.class, new Parameter[] {
                        new Parameter("id", 1),
                        new Parameter("id", 2),
                });

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(2, entities.size());
        assertEquals(1 , entities.get(0).getId());
        assertEquals(2 , entities.get(1).getId());
    }

    @Test
    public void testGetListEntityWithMultiDifferentTypeParams() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        List<Item> entities =
                entityIdentifierDao.getEntityList(sessionFactory.openSession(), Item.class, new Parameter[] {
                        new Parameter("id", 1),
                        new Parameter("name", "Test Entity 3"),
                });

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(2, entities.size());
        assertEquals(1 , entities.get(0).getId());
        assertEquals(3 , entities.get(1).getId());
    }

    @Test
    public void testGetEntityListWithNullParameters() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        List<Item> entities = entityIdentifierDao.getEntityList(sessionFactory.openSession(), Item.class);

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(3, entities.size());
    }

    @Test
    public void testGetEntityWithNullParameter() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityIdentifierDao.getEntity(sessionFactory.openSession(), Item.class, (Parameter[]) null);
        });
    }

    @Test
    public void testGetOptionalEntityWithNullParameters() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityIdentifierDao.getOptionalEntity(sessionFactory.openSession(), Item.class, (Parameter[]) null);
        });
    }

    @Test
    public void testGetEntityListAllException() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");

        assertThrows(RuntimeException.class, () -> {
            entityIdentifierDao.getEntityList(sessionFactory.openSession(), Object.class);
        });
    }

    @Test
    public void testGetEntityListGraphAllException() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");

        assertThrows(RuntimeException.class, () -> {
            entityIdentifierDao.getEntityListGraph(sessionFactory.openSession(), GRAPH_PATH, Object.class);
        });
    }

    private Employee getEmployee() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Relationship Root Entity");

        Dependent spouse = new Dependent();
        spouse.setId(1);
        spouse.setName("Dependent Entity");

        Dependent dependent1 = new Dependent();
        dependent1.setId(2);
        dependent1.setName("Dependent Entity");
        Dependent dependent2 = new Dependent();
        dependent2.setId(3);
        dependent2.setName("Dependent Entity");

        employee.setSpouse(spouse);
        employee.setDependentList(List.of(dependent1, dependent2));

        return employee;
    }

    private void checkEmployee(Employee expected, Employee actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());

        assertEquals(expected.getSpouse().getId(), actual.getSpouse().getId());
        assertEquals(expected.getSpouse().getName(), actual.getSpouse().getName());

        assertEquals(expected.getDependentList().get(0).getId(), actual.getDependentList().get(0).getId());
        assertEquals(expected.getDependentList().get(0).getName(), actual.getDependentList().get(0).getName());
        assertEquals(expected.getDependentList().get(1).getId(), actual.getDependentList().get(1).getId());
        assertEquals(expected.getDependentList().get(1).getName(), actual.getDependentList().get(1).getName());
    }

}
