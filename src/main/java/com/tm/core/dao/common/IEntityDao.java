package com.tm.core.dao.common;

public interface IEntityDao extends IEntityCommandDao, IFunctionCommandDao, IQueryDao {
    Class<?> getEntityClass();
}
