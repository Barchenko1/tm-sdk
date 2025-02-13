package com.tm.core.process.dao.identifier;

import com.tm.core.finder.parameter.Parameter;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface IQueryService {

    <E> E getEntity(Session session, Class<E> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Session session, Class<E> clazz, Parameter... parameters);
    <E> List<E> getEntityList(Session session, Class<E> clazz, Parameter... parameters);

    <E> E getEntityGraph(Session session, Class<E> clazz, String graphName, Parameter... parameters);
    <E> Optional<E> getOptionalEntityGraph(Session session, Class<E> clazz, String graphName, Parameter... parameters);
    <E> List<E> getEntityListGraph(Session session, Class<E> clazz, String graphName, Parameter... parameters);

    <E> E getEntityNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> Optional<E> getOptionalEntityNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... parameters);
    <E> List<E> getEntityListNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... parameters);
}
