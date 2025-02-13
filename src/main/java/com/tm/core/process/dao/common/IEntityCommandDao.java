package com.tm.core.process.dao.common;

import com.tm.core.finder.parameter.Parameter;

public interface IEntityCommandDao {
    <E> void persistEntity(E entity);
    <E> void mergeEntity(E entity);
    <E> void deleteEntity(E entity);

    <E> void findEntityAndUpdate(E entity, Parameter... parameters);
    <E> void findEntityAndDelete(Parameter... parameters);
}
