package com.tm.core.process.manager.common;

import com.tm.core.finder.parameter.Parameter;

public interface IEntityCommand {
    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);

    void deleteEntityByParameter(Parameter parameter);
}
