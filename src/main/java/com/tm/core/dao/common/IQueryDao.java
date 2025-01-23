package com.tm.core.dao.common;

import com.tm.core.processor.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IQueryDao {
    <E> List<E> getEntityList(Parameter... parameters);
    <E> List<E> getEntityGraphList(String graphName, Parameter... parameters);
    <E> E getEntity(Parameter... parameters);
    <E> E getEntityGraph(String graphName, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Parameter... parameters);
    <E> Optional<E> getOptionalEntityGraph(String graphName, Parameter... parameters);
}
