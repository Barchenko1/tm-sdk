package com.tm.core.dao.common;

import com.tm.core.processor.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;

public interface IQueryDao {
    <E> List<E> getEntityList(Parameter... parameters);
    <E> E getEntity(Parameter... parameters);
    <E> Optional<E> getOptionalEntity(Parameter... parameters);
}
