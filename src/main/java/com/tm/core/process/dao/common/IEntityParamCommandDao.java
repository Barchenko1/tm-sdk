package com.tm.core.process.dao.common;

import com.tm.core.finder.parameter.Parameter;

public interface IEntityParamCommandDao {
    <E> void findEntityAndUpdate(E entity, Parameter... parameters);
    <E> void findEntityAndDelete(Parameter... parameters);
}
