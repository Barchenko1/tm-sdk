package com.tm.core.test.dao;

import com.tm.core.dao.identifier.EntityIdentifierDao;
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

    private EntityIdentifierDao entityIdentifierDao;

    @BeforeEach
    public void setUpAll() {
        MockitoAnnotations.openMocks(this);
        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(new EntityTable(Item.class, "item"));
        entityIdentifierDao = new EntityIdentifierDao(entityMappingManager);
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
    public void testGetEntity() {
        loadDataSet("/datasets/single/searchAllItemEntityDataSet.yml");
        Item entity =
                entityIdentifierDao.getEntity(sessionFactory.openSession(), Item.class, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
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

}
