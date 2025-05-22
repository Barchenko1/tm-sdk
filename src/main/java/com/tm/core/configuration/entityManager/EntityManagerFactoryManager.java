package com.tm.core.configuration.entityManager;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.tm.core.configuration.dbType.DatabaseType.READ;
import static com.tm.core.configuration.dbType.DatabaseType.WRITE;

public class EntityManagerFactoryManager implements IEntityManagerFactoryManager {

    private static EntityManagerFactoryManager instance;
    private final Map<String, EntityManagerFactory> configurationReadEntityManagerFactoryMap;
    private final Map<String, EntityManagerFactory> configurationWriteEntityManagerFactoryMap;

    private EntityManagerFactoryManager(DatabaseTypeConfiguration dataBaseConfiguration) {
        this.configurationReadEntityManagerFactoryMap = new HashMap<>() {{
            if (dataBaseConfiguration.getDatabaseType() == DatabaseType.READ) {
                DatabaseConfigurationAnnotationClass[] readDatabaseConfigurations = dataBaseConfiguration.getConfigurations();
                if (readDatabaseConfigurations != null) {
                    for (DatabaseConfigurationAnnotationClass readConfiguration : readDatabaseConfigurations) {
                        put(readConfiguration.getConfigurationFileName(),
                                new ConfigurationEntityManagerFactory(readConfiguration.getConfigurationFileName()).configureEntityManagerFactory());
                    }
                }
            }
        }};
        this.configurationWriteEntityManagerFactoryMap = new HashMap<>() {{
            DatabaseConfigurationAnnotationClass[] writeDatabaseConfigurations = dataBaseConfiguration.getConfigurations();
            if (writeDatabaseConfigurations != null) {
                for (DatabaseConfigurationAnnotationClass writeConfiguration : writeDatabaseConfigurations) {
                    put(writeConfiguration.getConfigurationFileName(),
                            new ConfigurationEntityManagerFactory(writeConfiguration.getConfigurationFileName()).configureEntityManagerFactory());
                }
            }
        }};
    }

    public static synchronized EntityManagerFactoryManager getInstance(DatabaseTypeConfiguration databaseConfiguration) {
        if (instance == null) {
            instance = new EntityManagerFactoryManager(databaseConfiguration);
        }
        return instance;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory(DatabaseType databaseType, String configFileName) {
        if (READ.equals(databaseType)) {
            return this.configurationReadEntityManagerFactoryMap.get(configFileName);
        }
        if (WRITE.equals(databaseType)) {
            return this.configurationWriteEntityManagerFactoryMap.get(configFileName);
        }
        throw new IllegalArgumentException("Unsupported database type: " + databaseType);
    }

}
