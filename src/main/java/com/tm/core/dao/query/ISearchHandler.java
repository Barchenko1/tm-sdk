package com.tm.core.dao.query;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface ISearchHandler {
    <E> List<E> getEntityList(Class<?> clazz, Parameter... parameters);
    <E> E getEntity(Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Class<?> clazz, Parameter... parameters);

    <E> List<E> getEntityListGraph(Class<?> clazz, String graphName, Parameter... parameters);
    <E> E getEntityGraph(Class<?> clazz, String graphName, Parameter... parameters);
    <E> Optional<E> getOptionalEntityGraph(Class<?> clazz, String graphName, Parameter... parameters);

    <E> List<E> getEntityNamedQueryList(Class<?> clazz, String namedQuery, Parameter... parameters);
    <E> E getEntityNamedQuery(Class<?> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getOptionalEntityNamedQuery(Class<?> clazz, String namedQuery, Parameter... parameters);
}
