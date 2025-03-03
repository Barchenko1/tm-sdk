package com.tm.core.process.manager.common;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IEntityQuery {
    <E> E getGraphEntity(String graph, Parameter parameter);
    <E> Optional<E> getGraphOptionalEntity(String graph, Parameter parameter);
    <E> List<E> getGraphEntityList(String graph, Parameter parameter);

    <E, R> R getGraphEntityDto(String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
    <E, R> Optional<R> getGraphOptionalEntityDto(String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
    <E, R> List<R> getGraphEntityDtoList(String graph, Function<E, R> mapToDtoFunction);
    <E, R> List<R> getSubGraphEntityDtoList(String graph, Parameter parameter, Function<E, R> mapToDtoFunction);
}
