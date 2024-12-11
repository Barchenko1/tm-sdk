package com.tm.core.configuration.manager;

import com.tm.core.configuration.factory.ConfigurationSessionFactory;
import com.tm.core.configuration.factory.IConfigurationSessionFactory;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.tm.core.configuration.manager.DatabaseType.READ;
import static com.tm.core.configuration.manager.DatabaseType.WRITE;

public class SessionFactoryManager implements ISessionFactoryManager {

    private static SessionFactoryManager instance;
    private final Map<String, SessionFactory> configurationReadSessionFactoryMap;
    private final Map<String, SessionFactory> configurationWriteSessionFactoryMap;

    private SessionFactoryManager(DatabaseTypeConfiguration dataBaseConfiguration) {
        this.configurationReadSessionFactoryMap = new HashMap<>() {{
            if (dataBaseConfiguration.getDatabaseType() == DatabaseType.READ) {
                DatabaseConfigurationAnnotationClass[] readDatabaseConfigurations = dataBaseConfiguration.getConfigurations();
                if (readDatabaseConfigurations != null) {
                    for (DatabaseConfigurationAnnotationClass readConfiguration : readDatabaseConfigurations) {
                        put(readConfiguration.getConfigurationFileName(),
                                new ConfigurationSessionFactory(readConfiguration.getConfigurationFileName()).configureSessionFactory());
                    }
                }
            }
        }};
        this.configurationWriteSessionFactoryMap = new HashMap<>() {{
            DatabaseConfigurationAnnotationClass[] writeDatabaseConfigurations = dataBaseConfiguration.getConfigurations();
            if (writeDatabaseConfigurations != null) {
                for (DatabaseConfigurationAnnotationClass writeConfiguration : writeDatabaseConfigurations) {
                    put(writeConfiguration.getConfigurationFileName(),
                            new ConfigurationSessionFactory(writeConfiguration.getConfigurationFileName()).configureSessionFactory());
                }
            }
        }};
    }

    public static synchronized SessionFactoryManager getInstance(DatabaseTypeConfiguration databaseConfiguration) {
        if (instance == null) {
            instance = new SessionFactoryManager(databaseConfiguration);
        }
        return instance;
    }

    @Override
    public Supplier<SessionFactory> getSessionFactorySupplier(DatabaseType databaseType, String configFileName) {
        if (READ.equals(databaseType)) {
            return () -> this.configurationReadSessionFactoryMap.get(configFileName);
        }
        if (WRITE.equals(databaseType)) {
            return () -> this.configurationWriteSessionFactoryMap.get(configFileName);
        }
        throw new IllegalArgumentException("Unsupported database type: " + databaseType);
    }

    private IConfigurationSessionFactory createSessionFactory(String configFileName, Class<?>[] annotationClasses) {
        if (annotationClasses != null) {
            return new ConfigurationSessionFactory(configFileName, annotationClasses);
        } else {
            return new ConfigurationSessionFactory(configFileName);
        }
    }

}
