package com.tm.core.process.dao.query;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IFetchHandler {
    <E> List<E> getGraphEntityList(Class<E> clazz, String graphName, Parameter... parameters);
    <E> E getGraphEntity(Class<E> clazz, String graphName, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graphName, Parameter... parameters);

    <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters);

}
