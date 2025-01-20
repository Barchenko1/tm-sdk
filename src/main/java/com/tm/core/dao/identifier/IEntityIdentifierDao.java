package com.tm.core.dao.identifier;

import com.tm.core.processor.finder.parameter.Parameter;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IEntityIdentifierDao {

    <E> List<E> getEntityList(Session session, Class<?> clazz, Parameter... parameters);
    <E, R> List<R> getEntityListFunction(Session session, Class<?> clazz, Function<E, R> function, Parameter... parameters);

    <E> E getEntity(Session session, Class<?> clazz, Parameter... parameters);
    <E, R> R getEntityFunction(Session session, Class<?> clazz, Function<E, R> function, Parameter... parameters);

    <E> Optional<E> getOptionalEntity(Session session, Class<?> clazz, Parameter... parameters);
    <E, R> Optional<R> getOptionalEntityFunction(Session session, Class<?> clazz, Function<E, R> function, Parameter... parameters);
}
