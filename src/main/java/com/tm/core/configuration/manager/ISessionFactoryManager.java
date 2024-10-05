package com.tm.core.configuration.manager;

import org.hibernate.SessionFactory;

import java.util.function.Supplier;

public interface ISessionFactoryManager {

    Supplier<SessionFactory> getSessionFactorySupplier(DatabaseType databaseType);
}
