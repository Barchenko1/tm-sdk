package com.tm.core.mapper;

public interface IDtoEntityMapper {
    <E, R> void mapDtoToEntity(E dtoObject, R entityObject, String bindKey);
}
