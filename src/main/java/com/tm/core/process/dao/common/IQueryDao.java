package com.tm.core.process.dao.common;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IQueryDao {
    <E> List<E> getEntityList(Parameter... parameters);
    <E> List<E> getEntityGraphList(String graphName, Parameter... parameters);
    <E> List<E> getEntityNamedQueryList(String namedQuery, Parameter... parameters);
    <E> E getEntity(Parameter... parameters);
    <E> E getEntityGraph(String graphName, Parameter... parameters);
    <E> E getEntityNamedQuery(String namedQuery, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Parameter... parameters);
    <E> Optional<E> getOptionalEntityGraph(String graphName, Parameter... parameters);
    <E> Optional<E> getOptionalEntityNamedQuery(String namedQuery, Parameter... parameters);
}
