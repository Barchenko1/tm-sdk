package com.tm.core.process.dao.query;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IQueryService {

    <E> E getEntityByDefaultNamedQuery(EntityManager entityManager, Class<E> clazz, Parameter... parameters);

    <E> E getGraphEntity(EntityManager entityManager, Class<E> clazz, String graph, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntity(EntityManager entityManager, Class<E> clazz, String graph, Parameter... parameters);
    <E> List<E> getGraphEntityList(EntityManager entityManager, Class<E> clazz, String graph, Parameter... parameters);

    <E> E getNamedQueryEntity(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntity(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> List<E> getNamedQueryEntityList(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> List<E> getNamedQueryEntityMap(EntityManager entityManager, Class<E> clazz, String namedQuery, Map<String, List<?>> parameters);

}
