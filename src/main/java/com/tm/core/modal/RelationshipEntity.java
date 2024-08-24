package com.tm.core.modal;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RelationshipEntity {
    private Map<Integer, List<RelationshipNode>> relationshipEntityMap;

    public RelationshipEntity(Map<Integer, List<RelationshipNode>> relationshipEntityMap) {
        this.relationshipEntityMap = relationshipEntityMap;
    }

    public Map<Integer, List<RelationshipNode>> getRelationshipEntityMap() {
        return relationshipEntityMap;
    }

    public Set<Integer> getKeys() {
        return relationshipEntityMap.keySet();
    }

    public List<RelationshipNode> getValues(int priority) {
        return relationshipEntityMap.get(priority);
    }

    public void addRelationshipEntity(RelationshipNode relationshipNode) {
        relationshipEntityMap.get(relationshipNode.getPriority()).add(relationshipNode);
    }

}
