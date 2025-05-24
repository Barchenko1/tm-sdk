package com.tm.core.process.manager;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IQuery {

    <E> E getGraphEntity(Class<E> clazz, String graph, Parameter parameter);
    <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter parameter);
    <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter parameter);

    <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter parameter);
    <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter parameter);
    <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter parameter);

    <E, R> R getGraphEntityDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
    <E, R> Optional<R> getGraphOptionalEntityDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
    <E, R> List<R> getGraphEntityDtoList(Class<E> clazz, String graph, Function<E, R> mapToDtoFunction);
    <E, R> List<R> getSubGraphEntityDtoList(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
}
