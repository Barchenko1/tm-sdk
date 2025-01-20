package com.tm.core.dao.common;

import com.tm.core.processor.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IQueryDao {
    <E> List<E> getEntityList(Parameter... parameters);
    <E, R> List<R> getEntityListFunction(Function<E, R> function, Parameter... parameters);
    <E> E getEntity(Parameter... parameters);
    <E, R> R getEntityFunction(Function<E, R> function, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Parameter... parameters);
    <E, R> Optional<R> getOptionalEntityFunction(Function<E, R> function, Parameter... parameters);
}
