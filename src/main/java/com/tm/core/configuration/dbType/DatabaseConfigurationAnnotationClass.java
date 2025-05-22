package com.tm.core.configuration.dbType;

public class DatabaseConfigurationAnnotationClass {
    private final String configurationFileName;
    private Class<?>[] annotationClasses;

    public DatabaseConfigurationAnnotationClass(String configurationFileName) {
        this.configurationFileName = configurationFileName;
    }

    public DatabaseConfigurationAnnotationClass(String configurationFileName, Class<?>[] annotationClasses) {
        this.configurationFileName = configurationFileName;
        this.annotationClasses = annotationClasses;
    }

    public String getConfigurationFileName() {
        return configurationFileName;
    }

    public Class<?>[] getAnnotationClasses() {
        return annotationClasses;
    }
}
