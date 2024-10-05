package com.tm.core.test.finder;

import com.tm.core.modal.relationship.DependentTestEntity;
import com.tm.core.modal.relationship.RelationshipRootTestEntity;
import com.tm.core.modal.relationship.SingleDependentTestEntity;
import com.tm.core.modal.single.SingleTestEntity;
import com.tm.core.modal.transitive.TransitiveSelfTestEntity;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.scanner.EntityScanner;
import com.tm.core.processor.finder.scanner.IEntityScanner;
import com.tm.core.processor.finder.table.EntityTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EntityScannerTest {

    @Mock
    private IEntityMappingManager entityMappingManager;

    private IEntityScanner entityScanner;

    @Captor
    private ArgumentCaptor<EntityTable> entityTableCaptor;

    private final static String ENTITY_PACKAGE = "com.tm.core.modal";

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testGetEntityTables() {
        entityScanner = new EntityScanner(entityMappingManager, ENTITY_PACKAGE);
        verify(entityMappingManager, times(5)).addEntityTable(entityTableCaptor.capture());

        assertEquals(5, entityTableCaptor.getAllValues().size());
        List<EntityTable> capturedTables = entityTableCaptor.getAllValues();
        capturedTables.sort(Comparator.comparing(e -> e.getClazz().getName()));

        EntityTable capturedTable1 = capturedTables.get(0);
        assertEquals(DependentTestEntity.class, capturedTable1.getClazz());
        assertEquals("dependentTestEntity", capturedTable1.getTableName());

        EntityTable capturedTable2 = capturedTables.get(1);
        assertEquals(RelationshipRootTestEntity.class, capturedTable2.getClazz());
        assertEquals("relationshipRootTestEntity", capturedTable2.getTableName());

        EntityTable capturedTable3 = capturedTables.get(2);
        assertEquals(SingleDependentTestEntity.class, capturedTable3.getClazz());
        assertEquals("singleDependentTestEntity", capturedTable3.getTableName());

        EntityTable capturedTable4 = capturedTables.get(3);
        assertEquals(SingleTestEntity.class, capturedTable4.getClazz());
        assertEquals("singleTestEntity", capturedTable4.getTableName());

        EntityTable capturedTable5 = capturedTables.get(4);
        assertEquals(TransitiveSelfTestEntity.class, capturedTable5.getClazz());
        assertEquals("transitiveSelfTestEntity", capturedTable5.getTableName());
    }

    @Test
    public void testGetEntityTable_Failed() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> new EntityScanner(entityMappingManager, "#"));

        assertEquals("Failed to get classes in package: #", exception.getMessage());
    }
}
