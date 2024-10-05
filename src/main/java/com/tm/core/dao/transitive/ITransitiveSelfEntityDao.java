package com.tm.core.dao.transitive;

import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.util.TransitiveSelfEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ITransitiveSelfEntityDao {

    <E extends TransitiveSelfEntity> void saveEntityTree(E entity);
    <E extends TransitiveSelfEntity> void updateEntityTree(E entity, Parameter... parameters);
    <E extends TransitiveSelfEntity> void deleteEntityTree(Parameter... parameters);

    <E extends TransitiveSelfEntity> List<E> getTransitiveSelfEntityList(Parameter... parameters);
    <E extends TransitiveSelfEntity> E getTransitiveSelfEntity(Parameter... parameters);
    <E extends TransitiveSelfEntity> Optional<E> getOptionalTransitiveSelfEntity(Parameter... parameters);
    <E> List<E> getTransitiveSelfEntityList(Class<?> clazz, Parameter... parameters);
    <E> E getTransitiveSelfEntity(Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalTransitiveSelfEntity(Class<?> clazz, Parameter... parameters);

    <E extends TransitiveSelfEntity> Map<TransitiveSelfEnum, List<E>> getTransitiveSelfEntitiesTree();
}
