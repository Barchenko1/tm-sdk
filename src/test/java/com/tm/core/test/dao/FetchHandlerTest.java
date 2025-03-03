package com.tm.core.test.dao;

import com.tm.core.process.dao.identifier.QueryService;
import com.tm.core.process.dao.query.FetchHandler;
import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.modal.relationship.Dependent;
import com.tm.core.modal.relationship.Employee;
import com.tm.core.modal.relationship.Item;
import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.table.EntityTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FetchHandlerTest extends AbstractDaoTest {

    private static final String GRAPH_PATH = "Employee.full";
    private final String NAMED_QUERY_NAME_ONE = "Employee.findByIdWithJoins";
    private final String NAMED_QUERY_NAME_ALL = "Employee.findAllWithJoins";
    private static FetchHandler fetchHandler;

    @BeforeAll
    public static void setUpAll() {
        IEntityMappingManager entityMappingManager = getEntityMappingManager();
        IQueryService queryService = new QueryService(entityMappingManager);
        fetchHandler = new FetchHandler(sessionFactory, queryService);
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
    void getGraph_EntityList_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        List<Employee> result =
                fetchHandler.getGraphEntityList(Employee.class, GRAPH_PATH,parameter);

        assertEquals(1, result.size());
        checkEmployee(expectedEmployee, result.get(0));
    }

    @Test
    void getGraph_OptionalEntity_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Optional<Employee> optional =
                fetchHandler.getGraphOptionalEntity(Employee.class, GRAPH_PATH, parameter);

        assertTrue(optional.isPresent());
        Employee result = optional.get();
        assertEquals(1L, result.getId());
        checkEmployee(expectedEmployee, result);
    }

    @Test
    void getGraph_Entity_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Employee result = fetchHandler.getGraphEntity(Employee.class, GRAPH_PATH, parameter);
        checkEmployee(expectedEmployee, result);
    }

    @Test
    void getNamedQueryEntityList_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        List<Employee> result =
                fetchHandler.getNamedQueryEntityList(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

        assertEquals(1, result.size());
        checkEmployee(expectedEmployee, result.get(0));
    }

    @Test
    void getNamedQueryEntityAllList_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        List<Employee> result =
                fetchHandler.getNamedQueryEntityList(Employee.class, NAMED_QUERY_NAME_ALL);

        assertEquals(2, result.size());
    }

    @Test
    void getNamedQuery_OptionalEntity_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Optional<Employee> optional =
                fetchHandler.getNamedQueryOptionalEntity(Employee.class, NAMED_QUERY_NAME_ONE, parameter);

        assertTrue(optional.isPresent());
        Employee result = optional.get();
        assertEquals(1L, result.getId());
        checkEmployee(expectedEmployee, result);
    }

    @Test
    void getNamedQuery_Entity_success() {
        loadDataSet("/datasets/relationship/testAllRelationshipTestEntityDataSet.yml");
        Parameter parameter = new Parameter("id", 1L);
        Employee expectedEmployee = getEmployee();
        Employee result = fetchHandler.getNamedQueryEntity(Employee.class, NAMED_QUERY_NAME_ONE, parameter);
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
