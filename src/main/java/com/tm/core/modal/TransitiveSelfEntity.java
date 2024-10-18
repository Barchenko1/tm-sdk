package com.tm.core.modal;

import java.util.List;

public abstract class TransitiveSelfEntity {
    public abstract <E extends TransitiveSelfEntity> E getParent();
    public abstract void setParent(TransitiveSelfEntity parent);
    public abstract String getRootField();
    public abstract <E extends TransitiveSelfEntity> List<E> getChildNodeList();
    public abstract <E extends TransitiveSelfEntity> void setChildNodeList(List<E> childNodeList);

    public void addChildTransitiveEntity(TransitiveSelfEntity childTransitiveSelfEntity) {
        this.getChildNodeList().add(childTransitiveSelfEntity);
        childTransitiveSelfEntity.setParent(this);
    }

    public void removeChildTransitiveEntity(TransitiveSelfEntity childTransitiveSelfEntity) {
        this.getChildNodeList().remove(childTransitiveSelfEntity);
        childTransitiveSelfEntity.setParent(null);
    }
}
