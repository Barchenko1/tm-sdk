package com.tm.core.test.dao;

import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.query.SearchWrapper;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.table.EntityTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchWrapperTest extends AbstractDaoTest {

    private static final String GRAPH_PATH = "Employee.full";
    private final String NAMED_QUERY_NAME_ONE = "Employee.findByIdWithJoins";
    private final String NAMED_QUERY_NAME_ALL = "Employee.findAllWithJoins";
    private static SearchWrapper searchWrapper;

    @BeforeAll
    public static void setUpAll() {
        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        IEntityIdentifierDao entityIdentifierDao = new EntityIdentifierDao(entityMappingManager);
        searchWrapper = new SearchWrapper(sessionFactory, entityIdentifierDao);
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
    void getEntityList_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        List<Item> result =
                searchWrapper.getEntityList(Item.class, parameter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Entity 1", result.get(0).getName());
    }

    @Test
    void getOptionalEntity_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Optional<Item> optional =
                searchWrapper.getOptionalEntity(Item.class, parameter);

        assertTrue(optional.isPresent());
        Item result = optional.get();
        assertEquals(1L, result.getId());
        assertEquals("Test Entity 1", result.getName());
    }

    @Test
    void getEntity_success() {
        loadDataSet("/datasets/single/testItemEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);

        Item result = searchWrapper.getEntity(Item.class, parameter);
        assertEquals(1L, result.getId());
        assertEquals("Test Entity 1", result.getName());
    }

    @Test
    void getEntityListGraph_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        List<Employee> result =
                searchWrapper.getEntityListGraph(Employee.class, GRAPH_PATH,parameter);

        assertEquals(1, result.size());
        checkEmployee(expectedEmployee, result.get(0));
    }

    @Test
    void getOptionalEntityGraph_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Optional<Employee> optional =
                searchWrapper.getOptionalEntityGraph(Employee.class, GRAPH_PATH, parameter);

        assertTrue(optional.isPresent());
        Employee result = optional.get();
        assertEquals(1L, result.getId());
        checkEmployee(expectedEmployee, result);
    }

    @Test
    void getEntityGraph_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Employee result = searchWrapper.getEntityGraph(Employee.class, GRAPH_PATH, parameter);
        checkEmployee(expectedEmployee, result);
    }

    @Test
    void getEntityNamedQueryList_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        List<Employee> result =
                searchWrapper.getEntityNamedQueryList(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

        assertEquals(1, result.size());
        checkEmployee(expectedEmployee, result.get(0));
    }

    @Test
    void getEntityNamedQueryAllList_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        List<Employee> result =
                searchWrapper.getEntityNamedQueryList(Employee.class, NAMED_QUERY_NAME_ALL);

        assertEquals(2, result.size());
    }

    @Test
    void getOptionalEntityNamedQuery_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Optional<Employee> optional =
                searchWrapper.getOptionalEntityNamedQuery(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

        assertTrue(optional.isPresent());
        Employee result = optional.get();
        assertEquals(1L, result.getId());
        checkEmployee(expectedEmployee, result);
    }

    @Test
    void getEntityNamedQuery_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Employee result = searchWrapper.getEntityNamedQuery(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
        checkEmployee(expectedEmployee, result);
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
