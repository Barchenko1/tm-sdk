package com.tm.core.modal;

public class RelationshipNode {
    private Object entity;
    private int priority;

    public RelationshipNode(Object entity, int priority) {
        this.entity = entity;
        this.priority = priority;
    }

    public Object getEntity() {
        return entity;
    }

    public int getPriority() {
        return priority;
    }
}
