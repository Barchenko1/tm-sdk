package com.tm.core.dao.identifier;

import com.tm.core.processor.finder.parameter.Parameter;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface IEntityIdentifierDao {

    <E> List<E> getEntityList(Session session, Class<?> clazz, Parameter... parameters);
    <E> List<E> getEntityListGraph(Session session, String graphName, Class<?> clazz, Parameter... parameters);
    <E> E getEntity(Session session, Class<?> clazz, Parameter... parameters);
    <E> E getEntityGraph(Session session, String graphName, Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Session session, Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntityGraph(Session session, String graphName, Class<?> clazz, Parameter... parameters);
}
