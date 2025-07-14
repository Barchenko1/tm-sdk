package com.tm.core.process.manager.generic;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IQuery {
    <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters);
    <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters);

    <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters);
    <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters);

    <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters);

    <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters);
}
