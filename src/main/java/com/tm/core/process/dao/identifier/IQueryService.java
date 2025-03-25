package com.tm.core.process.dao.identifier;

import com.tm.core.finder.parameter.Parameter;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IQueryService {

    <E> E getEntityByDefaultNamedQuery(Session session, Class<E> clazz, Parameter... parameters);

    <E> E getGraphEntity(Session session, Class<E> clazz, String graphName, Parameter... parameters);
    <E> Optional<E> getGraphOptionalEntity(Session session, Class<E> clazz, String graphName, Parameter... parameters);
    <E> List<E> getGraphEntityList(Session session, Class<E> clazz, String graphName, Parameter... parameters);

    <E> E getNamedQueryEntity(Session session, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getNamedQueryOptionalEntity(Session session, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> List<E> getNamedQueryEntityList(Session session, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> List<E> getNamedQueryEntityMap(Session session, Class<E> clazz, String namedQuery, Map<String, List<?>> parameters);

}
