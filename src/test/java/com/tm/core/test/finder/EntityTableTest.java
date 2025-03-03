package com.tm.core.test.finder;

import com.tm.core.finder.table.EntityTable;
import com.tm.core.modal.relationship.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityTableTest {

    private EntityTable entityTable;

    @BeforeEach
    void setUp() {
        entityTable = new EntityTable(Employee.class, "Employee");
    }

    @Test
    void constructor_ShouldInitializeFields() {
        assertEquals(Employee.class, entityTable.getClazz());
        assertEquals("SELECT e FROM Employee e", entityTable.getSelectAllJqlQuery());
    }

    @Test
    void createFindQuery_NoParameters_ThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityTable.createFindJqlQuery();
        });

        assertEquals("params cannot be null or empty", exception.getMessage());
    }

}
