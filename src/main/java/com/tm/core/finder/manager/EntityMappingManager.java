package com.tm.core.finder.manager;

import com.tm.core.finder.table.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class EntityMappingManager implements IEntityMappingManager {
    private final Map<Class<?>, EntityTable> entityTableMap;

    public EntityMappingManager() {
        this.entityTableMap = new HashMap<>();
    }

    @Override
    public void addEntityTable(EntityTable entityTable) {
        entityTableMap.put(entityTable.getClazz(), entityTable);
    }

    @Override
    public EntityTable getEntityTable(Class<?> clazz) {
        return entityTableMap.get(clazz);
    }


}
