package com.tm.core.dao.common;

import org.hibernate.SessionFactory;

public interface IEntityDao extends IEntityCommandDao, IFunctionCommandDao, IQueryDao {
    Class<?> getEntityClass();
    SessionFactory getSessionFactory();
}
