package com.tm.core.modal;

import java.util.List;

public abstract class TransitiveSelfEntity {
    public abstract <E extends TransitiveSelfEntity> E getParent();
    public abstract void setParent(TransitiveSelfEntity parent);
    public abstract String getRootField();
    public abstract <E extends TransitiveSelfEntity> List<E> getChildNodeList();
}
