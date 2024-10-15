package com.tm.core.dao.general;

import com.tm.core.modal.GeneralEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IGeneralEntityDao {
    void saveGeneralEntity(GeneralEntity generalEntity);
    void saveGeneralEntity(Consumer<Session> consumer);
    <E> void updateGeneralEntity(Supplier<E> supplier);
    void updateGeneralEntity(Consumer<Session> consumer);
    void deleteGeneralEntity(GeneralEntity generalEntity);
    void deleteGeneralEntity(Consumer<Session> consumer);
    <E> void deleteGeneralEntity(Class<?> clazz, Parameter... parameters);
    <E> void deleteGeneralEntity(Parameter... parameters);

    <E> List<E> getGeneralEntityList(Class<?> clazz, Parameter... parameters);
    <E> E getGeneralEntity(Class<?> clazz, Parameter... parameters);
    <E> Optional<E> getOptionalGeneralEntity(Class<?> clazz, Parameter... parameters);
    <E> List<E> getGeneralEntityList(Parameter... parameters);
    <E> E getGeneralEntity(Parameter... parameters);
    <E> Optional<E> getOptionalGeneralEntity(Parameter... parameters);

    <E> E initializeEntity(Class<?> clazz, long id);
}
