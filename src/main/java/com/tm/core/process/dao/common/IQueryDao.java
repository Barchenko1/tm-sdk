package com.tm.core.process.dao.common;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IQueryDao {
    <E> List<E> getGraphEntityList(String graphName, Parameter... parameters);
    <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters);
    <E> E getGraphEntity(String graphName, Parameter... parameters);
    <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntity(String graphName, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters);
}
