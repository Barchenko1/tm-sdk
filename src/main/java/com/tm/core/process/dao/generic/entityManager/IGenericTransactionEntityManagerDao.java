package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.process.dao.generic.IEntityQueryDao;
import com.tm.core.process.dao.generic.IParamCommandDao;
import jakarta.persistence.EntityManager;

public interface IGenericTransactionEntityManagerDao extends IParamCommandDao, IEntityQueryDao {
    EntityManager getEntityManager();
}
