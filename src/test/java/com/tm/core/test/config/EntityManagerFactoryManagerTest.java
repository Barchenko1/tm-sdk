package com.tm.core.test.config;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import com.tm.core.modal.relationship.Item;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.tm.core.configuration.dbType.DatabaseType.WRITE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntityManagerFactoryManagerTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";
    private static final DatabaseTypeConfiguration DATABASE_TYPE_CONFIGURATION = new DatabaseTypeConfiguration(
            DatabaseType.WRITE, new DatabaseConfigurationAnnotationClass[] {
                new DatabaseConfigurationAnnotationClass(CONFIGURATION_FILE_NAME)
            }
    );

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanceField = EntityManagerFactoryManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void testGetInstanceWithSingleConfigFileName() {
        EntityManagerFactoryManager instance = EntityManagerFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
        assertNotNull(instance);
        EntityManagerFactory writeFactory = instance.getEntityManagerFactory(WRITE, CONFIGURATION_FILE_NAME);
        assertNotNull(writeFactory);
    }

    @Test
    void testGetInstanceWithConfigFileNameAndAnnotations() {
        Class<?>[] annotationClasses = new Class<?>[]{
                Item.class
        };
        DatabaseConfigurationAnnotationClass databaseConfigurationAnnotationClass =
                new DatabaseConfigurationAnnotationClass(CONFIGURATION_FILE_NAME, annotationClasses);
        DatabaseTypeConfiguration databaseTypeConfiguration =
                new DatabaseTypeConfiguration(WRITE, new DatabaseConfigurationAnnotationClass[] {databaseConfigurationAnnotationClass});
        EntityManagerFactoryManager instance = EntityManagerFactoryManager.getInstance(databaseTypeConfiguration);
        assertNotNull(instance);
        EntityManagerFactory writeFactory = instance.getEntityManagerFactory(WRITE, CONFIGURATION_FILE_NAME);
        assertNotNull(writeFactory);
    }
}
