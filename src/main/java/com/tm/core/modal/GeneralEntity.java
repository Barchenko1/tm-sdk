package com.tm.core.modal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeneralEntity {
    private final Map<Integer, List<Object>> entityMap;

    public GeneralEntity() {
        this.entityMap = new HashMap<>();
    }

    public Map<Integer, List<Object>> getEntityMap() {
        return entityMap;
    }

    public Set<Integer> getKeys() {
        return entityMap.keySet();
    }

    public List<Object> getValues(int priority) {
        return entityMap.get(priority);
    }

    public void addEntityPriority(int priority, Object entity) {
        if (entity instanceof List<?>) {
            entityMap.computeIfAbsent(priority, k -> new ArrayList<>()).addAll((List<?>) entity);
        } else {
            entityMap.computeIfAbsent(priority, k -> new ArrayList<>()).add(entity);
        }
    }

}
