package com.tm.core.process.manager.common;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IEntityManagerOperatorQuery {
    <E> List<E> getGraphEntityList(String graph, Parameter... parameters);
    <E> E getGraphEntity(String graph, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameters);

    <E> List<E> getGraphEntityListClose(String graph, Parameter... parameters);
    <E> E getGraphEntityClose(String graph, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntityClose(String graph, Parameter... parameters);

    <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters);
    <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters);

    <E> List<E> getNamedQueryEntityListClose(String namedQuery, Parameter... parameters);
    <E> E getNamedQueryEntityClose(String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntityClose(String namedQuery, Parameter... parameters);

    <E> E getNamedQueryEntity(String namedQuery);
    <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery);
    <E> List<E> getNamedQueryEntityList(String namedQuery);

}
