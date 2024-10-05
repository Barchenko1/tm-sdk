package com.tm.core.dao.query;

import com.tm.core.processor.finder.parameter.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface ISearchWrapper {
    <E> Supplier<List<E>> getEntityListSupplier(Class<?> clazz, Parameter... parameters);
    <E> Supplier<E> getEntitySupplier(Class<?> clazz, Parameter... parameters);
    <E> Supplier<Optional<E>> getOptionalEntitySupplier(Class<?> clazz, Parameter... parameters);

}
