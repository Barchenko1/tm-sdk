package com.tm.core.process.dao.generic;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IGenericQueryDao {
    <E> List<E> getEntityList(Class<E> clazz, Parameter... parameters);
    <E> List<E> getEntityGraphList(Class<E> clazz, String graphName, Parameter... parameters);
    <E> List<E> getEntityNamedQueryList(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> E getEntity(Class<E> clazz, Parameter... parameters);
    <E> E getEntityGraph(Class<E> clazz, String graphName, Parameter... parameters);
    <E> E getEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Class<E> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntityGraph(Class<E> clazz, String graphName, Parameter... parameters);
    <E> Optional<E> getOptionalEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter... parameters);
}
