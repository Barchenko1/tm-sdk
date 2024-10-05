package com.tm.core.util.helper;

public interface IEntityFieldHelper {
    <E> long findId(E entity);
    void setId(Object entity, Object id);
}
