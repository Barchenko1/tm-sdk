package com.tm.core.process.dao.common;

import com.tm.core.finder.parameter.Parameter;

public interface IParamCommandDao extends ICommandDao {
    <E> void findEntityAndUpdate(E entity, Parameter... parameters);
    <E> void findEntityAndDelete(Parameter... parameters);
}
