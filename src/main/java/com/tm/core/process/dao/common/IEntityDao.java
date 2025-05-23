package com.tm.core.process.dao.common;

import com.tm.core.process.dao.IFunctionCommandDao;
import org.hibernate.SessionFactory;

public interface IEntityDao extends ICommandDao, IFunctionCommandDao, IQueryDao {
    Class<?> getEntityClass();
    SessionFactory getSessionFactory();
}
