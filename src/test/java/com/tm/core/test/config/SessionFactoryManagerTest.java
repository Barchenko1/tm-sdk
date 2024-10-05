package com.tm.core.test.config;

import com.tm.core.configuration.manager.SessionFactoryManager;
import com.tm.core.modal.single.SingleTestEntity;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import static com.tm.core.configuration.manager.DatabaseType.READ;
import static com.tm.core.configuration.manager.DatabaseType.WRITE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionFactoryManagerTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanceField = SessionFactoryManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void testGetInstanceWithSingleConfigFileName() {
        SessionFactoryManager instance = SessionFactoryManager.getInstance(CONFIGURATION_FILE_NAME);
        assertNotNull(instance);
        Supplier<SessionFactory> writeFactorySupplier = instance.getSessionFactorySupplier(WRITE);
        assertNotNull(writeFactorySupplier.get());
    }

    @Test
    void testGetInstanceWithConfigFileNameAndAnnotations() {
        Class<?>[] annotationClasses = new Class<?>[]{
                SingleTestEntity.class
        };
        SessionFactoryManager instance = SessionFactoryManager.getInstance(CONFIGURATION_FILE_NAME, annotationClasses);
        assertNotNull(instance);
        Supplier<SessionFactory> writeFactorySupplier = instance.getSessionFactorySupplier(WRITE);
        assertNotNull(writeFactorySupplier.get());
    }

    @Test
    void testGetInstanceWithReadAndWriteConfigFileNames() {
        SessionFactoryManager instance = SessionFactoryManager.getInstance(CONFIGURATION_FILE_NAME, CONFIGURATION_FILE_NAME);
        assertNotNull(instance);
        Supplier<SessionFactory> readFactorySupplier = instance.getSessionFactorySupplier(READ);
        assertNotNull(readFactorySupplier.get());
    }

    @Test
    void testGetInstanceWithReadWriteConfigFilesAndAnnotations() {
        Class<?>[] annotationClasses = new Class<?>[]{
                SingleTestEntity.class
        };
        SessionFactoryManager instance = SessionFactoryManager.getInstance(CONFIGURATION_FILE_NAME, annotationClasses, CONFIGURATION_FILE_NAME, annotationClasses);
        assertNotNull(instance);
        Supplier<SessionFactory> writeFactorySupplier = instance.getSessionFactorySupplier(WRITE);
        assertNotNull(writeFactorySupplier.get());
    }
}
