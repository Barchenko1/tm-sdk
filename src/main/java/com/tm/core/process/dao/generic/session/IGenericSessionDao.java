package com.tm.core.process.dao.generic.session;

import com.tm.core.process.dao.IFunctionCommandDao;
import com.tm.core.process.dao.generic.ICommandDao;
import com.tm.core.process.dao.generic.IEntityQueryDao;
import org.hibernate.SessionFactory;

public interface IGenericSessionDao extends ICommandDao, IFunctionCommandDao, IEntityQueryDao {
    SessionFactory getSessionFactory();
}
