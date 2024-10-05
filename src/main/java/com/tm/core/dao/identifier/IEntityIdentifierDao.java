package com.tm.core.dao.identifier;

import com.tm.core.processor.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IEntityIdentifierDao {

    <E> List<E> getEntityList(Class<?> clazz, Parameter... parameters);
    <E> E getEntity(Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Class<?> clazz, Parameter... parameters);
}
