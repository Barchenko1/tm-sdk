package com.tm.core.dto;

import java.util.List;

public interface IDtoEntityDao {

    <E> E getDto(String sqlQuery, Class<?> clazz);
    <E> List<E> getDtoList(String sqlQuery, Class<?> clazz);
}
