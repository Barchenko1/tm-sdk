package com.tm.core.process.manager.generic;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IGenericEntityQuery {
    <E> E getEntity(Class<E> clazz, Parameter parameter);
    <E> Optional<E> getOptionalEntity(Class<E> clazz, Parameter parameter);
    <E> List<E> getEntityList(Class<E> clazz, Parameter parameter);

    <E> E getEntityGraph(Class<E> clazz, String graph, Parameter parameter);
    <E> Optional<E> getOptionalEntityGraph(Class<E> clazz, String graph, Parameter parameter);
    <E> List<E> getEntityGraphList(Class<E> clazz, String graph, Parameter parameter);

    <E> E getEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter parameter);
    <E> Optional<E> getOptionalEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter parameter);
    <E> List<E> getEntityNamedQueryList(Class<E> clazz, String namedQuery, Parameter parameter);

    <E, R> R getEntityGraphDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
    <E, R> Optional<R> getOptionalEntityGraphDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
    <E, R> List<R> getEntityGraphDtoList(Class<E> clazz, String graph, Function<E, R> mapToDtoFunction);
    <E, R> List<R> getSubEntityGraphDtoList(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
}
