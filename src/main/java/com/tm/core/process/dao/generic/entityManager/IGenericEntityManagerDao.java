package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.process.dao.generic.ICommandDao;
import com.tm.core.process.dao.generic.IEntityQueryDao;
import jakarta.persistence.EntityManager;

public interface IGenericEntityManagerDao extends ICommandDao, IEntityQueryDao {
    EntityManager getEntityManager();
}
