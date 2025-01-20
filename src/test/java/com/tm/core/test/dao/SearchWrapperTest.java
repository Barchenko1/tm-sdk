package com.tm.core.test.dao;

import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.query.SearchWrapper;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchWrapperTest extends AbstractDaoTest {

    private static SearchWrapper searchWrapper;

    @BeforeAll
    public static void setUpAll() {
        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(new EntityTable(Item.class, "item"));
        IEntityIdentifierDao entityIdentifierDao = new EntityIdentifierDao(entityMappingManager);
        searchWrapper = new SearchWrapper(sessionFactory, entityIdentifierDao);
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

}
