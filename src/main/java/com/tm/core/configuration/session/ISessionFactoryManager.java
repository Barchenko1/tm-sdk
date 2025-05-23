package com.tm.core.configuration.session;

import com.tm.core.configuration.dbType.DatabaseType;
import org.hibernate.SessionFactory;

public interface ISessionFactoryManager {

    SessionFactory getSessionFactory(DatabaseType databaseType, String configFileName);
}
