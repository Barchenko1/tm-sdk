package com.tm.core.dao.single;

import com.tm.core.processor.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface ISingleEntityDao {

    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);

    <E> void findEntityAndUpdate(E entity, Parameter... parameters);
    <E> void findEntityAndDelete(Parameter... parameters);

    void mutateEntity(String sqlQuery, Parameter... params);

    <E> List<E> getEntityList(Parameter... parameters);
    <E> List<E> getEntityList(Class<?> clazz, Parameter... parameters);
    <E> E getEntity(Parameter... parameters);
    <E> E getEntity(Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Class<?> clazz, Parameter... parameters);

}
