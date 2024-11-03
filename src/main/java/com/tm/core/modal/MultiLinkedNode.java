package com.tm.core.modal;

import java.util.List;


public abstract class MultiLinkedNode {
    public abstract <E extends MultiLinkedNode> E getPrevious();
    public abstract <E extends MultiLinkedNode> void setPrevious(E element);
    public abstract String getRootValue();
    public abstract <E extends MultiLinkedNode> List<E> getNextList();
    public abstract <E extends MultiLinkedNode> void setNextList(List<E> list);

    public void addNext(MultiLinkedNode nextNode) {
        nextNode.setPrevious(this);
        this.getNextList().add(nextNode);
    }

    public void removeNext(MultiLinkedNode nextNode) {
        nextNode.setPrevious(null);
        this.getNextList().remove(nextNode);
    }

}
