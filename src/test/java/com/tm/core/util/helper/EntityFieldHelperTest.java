package com.tm.core.util.helper;

import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class EntityFieldHelperTest {

    private EntityFieldHelper entityFieldHelper;

    @BeforeEach
    void setUp() {
        entityFieldHelper = new EntityFieldHelper();
    }

    static class TestEntityId {
        @Id
        private Long id = 100L;
    }

    static class InheritedEntity extends TestEntityId {}

    static class NoIdEntity {
        @SuppressWarnings("unused")
        private String name = "Test";
    }

    @Test
    void testFindId_whenEntityHasIdField() {
        TestEntityId entity = new TestEntityId();
        Long id = entityFieldHelper.findId(entity);
        assertEquals(100L, id);
    }

    @Test
    void testFindId_whenEntityInheritsIdField() {
        InheritedEntity entity = new InheritedEntity();
        Long id = entityFieldHelper.findId(entity);
        assertEquals(100L, id);
    }

    @Test
    void testFindId_whenNoIdFieldFound() {
        NoIdEntity entity = new NoIdEntity();
        Exception exception = assertThrows(RuntimeException.class, () -> entityFieldHelper.findId(entity));
        assertEquals("No id field found in class hierarchy", exception.getMessage());
    }

    @Test
    void testSetId_whenEntityHasIdField() {
        TestEntityId entity = new TestEntityId();
        entityFieldHelper.setId(entity, 300L);
        assertEquals(300L, entityFieldHelper.findId(entity));
    }

    @Test
    void testSetId_whenEntityInheritsIdField() {
        InheritedEntity entity = new InheritedEntity();
        entityFieldHelper.setId(entity, 400L);
        assertEquals(400L, entityFieldHelper.findId(entity));
    }

    @Test
    void testSetId_whenNoIdFieldFound() {
        NoIdEntity entity = new NoIdEntity();
        Exception exception = assertThrows(RuntimeException.class, () -> entityFieldHelper.setId(entity, 600L));
        assertTrue(exception.getMessage().contains("ID field not found in entity class"));
    }

    @Test
    void testFindIdThrowsExceptionWhenFieldIsNotAccessible() {
        class TestEntity {
            @SuppressWarnings("unused")
            private Long id;
        }

        TestEntity testEntity = new TestEntity();

        Field idField = TestEntity.class.getDeclaredFields()[0];
        idField.setAccessible(false); // Simulate field not being accessible

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityFieldHelper.findId(testEntity);
        });

        assertEquals("No id field found in class hierarchy", exception.getMessage());
    }

    @Test
    void testFindIdThrowsExceptionWhenNoIdFieldFound() {
        // Arrange: Create a test entity without an 'id' field.
        class TestEntityWithoutId {}

        TestEntityWithoutId testEntity = new TestEntityWithoutId();

        // Act & Assert: Expect a RuntimeException to be thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityFieldHelper.findId(testEntity);
        });

        assertEquals("No id field found in class hierarchy", exception.getMessage());
    }

    @Test
    void testSetIdThrowsExceptionWhenFieldIsNotAccessible() {
        class TestEntity {
            @SuppressWarnings("unused")
            private Long id;
        }

        TestEntity testEntity = new TestEntity();

        Field idField = TestEntity.class.getDeclaredFields()[0];
        idField.setAccessible(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityFieldHelper.setId(testEntity, 123L);
        });

        assertEquals("ID field not found in entity class: class com.tm.core.util.helper.EntityFieldHelperTest$2TestEntity", exception.getMessage());
    }

    @Test
    void testSetIdThrowsExceptionWhenIdFieldNotFound() {
        // Arrange: Create a test entity without an 'id' field.
        class TestEntityWithoutId {}

        TestEntityWithoutId testEntity = new TestEntityWithoutId();

        // Act & Assert: Expect a RuntimeException to be thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityFieldHelper.setId(testEntity, 123L);
        });

        assertEquals("ID field not found in entity class: class com.tm.core.util.helper.EntityFieldHelperTest$2TestEntityWithoutId", exception.getMessage());
    }
}
