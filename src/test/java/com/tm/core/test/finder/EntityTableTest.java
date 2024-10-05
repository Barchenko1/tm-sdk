package com.tm.core.test.finder;

import com.tm.core.processor.finder.table.EntityTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityTableTest {

    private EntityTable entityTable;

    @BeforeEach
    void setUp() {
        entityTable = new EntityTable(Object.class, "my_table");
    }

    @Test
    void constructor_ShouldInitializeFields() {
        assertEquals(Object.class, entityTable.getClazz());
        assertEquals("SELECT * FROM my_table", entityTable.getSelectAllQuery());
    }

    @Test
    void createFindQuery_NoParameters_ThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityTable.createFindQuery();
        });

        assertEquals("params cannot be null or empty", exception.getMessage());
    }

}
