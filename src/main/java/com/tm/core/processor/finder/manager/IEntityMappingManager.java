package com.tm.core.processor.finder.manager;

import com.tm.core.processor.finder.table.EntityTable;

public interface IEntityMappingManager {

    void addEntityTable(EntityTable entityTable);
    EntityTable getEntityTable(Class<?> clazz);
}
