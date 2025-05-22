package com.tm.core.configuration.dbType;

public class DatabaseTypeConfiguration {

    private final DatabaseType databaseType;
    private final DatabaseConfigurationAnnotationClass[] configurations;


    public DatabaseTypeConfiguration(DatabaseType databaseType, DatabaseConfigurationAnnotationClass[] configurations) {
        this.databaseType = databaseType;
        this.configurations = configurations;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public DatabaseConfigurationAnnotationClass[] getConfigurations() {
        return configurations;
    }
}
