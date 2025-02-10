package com.tm.core.finder.manager;

import com.tm.core.finder.table.EntityTable;

public interface IEntityMappingManager {

    void addEntityTable(EntityTable entityTable);
    EntityTable getEntityTable(Class<?> clazz);
}
