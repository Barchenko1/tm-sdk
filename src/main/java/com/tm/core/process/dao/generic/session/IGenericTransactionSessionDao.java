package com.tm.core.process.dao.generic.session;

import com.tm.core.process.dao.IFunctionCommandDao;
import com.tm.core.process.dao.generic.IEntityQueryDao;
import com.tm.core.process.dao.generic.IParamCommandDao;
import org.hibernate.SessionFactory;

public interface IGenericTransactionSessionDao extends IParamCommandDao, IFunctionCommandDao, IEntityQueryDao {
    SessionFactory getSessionFactory();
}
