package com.tm.core.test.config;

import com.tm.core.configuration.manager.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.manager.DatabaseType;
import com.tm.core.configuration.manager.DatabaseTypeConfiguration;
import com.tm.core.configuration.manager.SessionFactoryManager;
import com.tm.core.modal.single.SingleTestEntity;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.tm.core.configuration.manager.DatabaseType.READ;
import static com.tm.core.configuration.manager.DatabaseType.WRITE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionFactoryManagerTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";
    private static final DatabaseTypeConfiguration DATABASE_TYPE_CONFIGURATION = new DatabaseTypeConfiguration(
            DatabaseType.WRITE, new DatabaseConfigurationAnnotationClass[]{
            new DatabaseConfigurationAnnotationClass(CONFIGURATION_FILE_NAME)
    }
    );

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanceField = SessionFactoryManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void testGetInstanceWithSingleConfigFileName() {
        SessionFactoryManager instance = SessionFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
        assertNotNull(instance);
        Supplier<SessionFactory> writeFactorySupplier = instance.getSessionFactorySupplier(WRITE, CONFIGURATION_FILE_NAME);
        assertNotNull(writeFactorySupplier.get());
    }

    @Test
    void testGetInstanceWithConfigFileNameAndAnnotations() {
        Class<?>[] annotationClasses = new Class<?>[]{
                SingleTestEntity.class
        };
        DatabaseConfigurationAnnotationClass databaseConfigurationAnnotationClass =
                new DatabaseConfigurationAnnotationClass(CONFIGURATION_FILE_NAME, annotationClasses);
        DatabaseTypeConfiguration databaseTypeConfiguration =
                new DatabaseTypeConfiguration(WRITE, new DatabaseConfigurationAnnotationClass[] {databaseConfigurationAnnotationClass});
        SessionFactoryManager instance = SessionFactoryManager.getInstance(databaseTypeConfiguration);
        assertNotNull(instance);
        Supplier<SessionFactory> writeFactorySupplier = instance.getSessionFactorySupplier(WRITE, CONFIGURATION_FILE_NAME);
        assertNotNull(writeFactorySupplier.get());
    }
}
