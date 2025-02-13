package com.tm.core.process.manager.generic;

import com.tm.core.finder.parameter.Parameter;

public interface IGenericEntityCommand {
    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);

    <E> void deleteEntityByParameter(Class<E> clazz, Parameter parameter);
}
