package com.tm.core.process.dao.generic;

import com.tm.core.finder.parameter.Parameter;

public interface IParamCommandDao extends ICommandDao {
    <E> void findEntityAndUpdate(Class<E> clazz, E entity, Parameter... parameters);
    <E> void findEntityAndDelete(Class<E> clazz, Parameter... parameters);
}
