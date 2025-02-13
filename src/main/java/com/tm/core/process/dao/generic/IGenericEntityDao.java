package com.tm.core.process.dao.generic;

import com.tm.core.process.dao.IFunctionCommandDao;
import org.hibernate.SessionFactory;

public interface IGenericEntityDao extends IEntityCommandDao, IFunctionCommandDao, IGenericQueryDao {
    SessionFactory getSessionFactory();
}
