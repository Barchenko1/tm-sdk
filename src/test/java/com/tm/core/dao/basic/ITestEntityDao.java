package com.tm.core.dao.basic;

import com.tm.core.dao.IEntityDao;

import java.util.List;
import java.util.Optional;

public interface ITestEntityDao extends IEntityDao {

    <E> List<E> getAllTestEntities();

    <E> Optional<E> getTestEntityById(long id);

    <E> Optional<E> getTestEntityByUser(String name);

    <E> Optional<E> getTestEntityByEmail(String email);

}
