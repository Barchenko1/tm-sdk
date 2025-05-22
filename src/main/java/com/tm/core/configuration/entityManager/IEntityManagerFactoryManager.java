package com.tm.core.configuration.entityManager;

import com.tm.core.configuration.dbType.DatabaseType;
import jakarta.persistence.EntityManagerFactory;

public interface IEntityManagerFactoryManager {

    EntityManagerFactory getEntityManagerFactory(DatabaseType databaseType, String configFileName);
}
