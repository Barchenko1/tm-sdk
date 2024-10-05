package com.tm.core.test.util;

import com.tm.core.util.properties.ConfigurationFileProvider;
import com.tm.core.util.properties.IConfigurationFileProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationFileProviderTest {

    private static final String ROOT_PATH = "src/test/resources/";
    private static final String FILE_NAME = "hikari.db.properties";

    @Test
    void loadHibernateConfigurationFile_FileExists_ReturnsProperties() throws IOException {
        String fileName = ROOT_PATH + FILE_NAME;
        IConfigurationFileProvider provider = new ConfigurationFileProvider();

        Properties properties = provider.loadHibernateConfigurationFile(fileName);

        assertNotNull(properties);
    }

    @Test
    void loadHibernateConfigurationFile_FileNotFound_ThrowsRuntimeException() {
        String fileName = "nonexistent.properties";
        IConfigurationFileProvider provider = new ConfigurationFileProvider();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            provider.loadHibernateConfigurationFile(fileName);
        });
        assertEquals("File not found: " + fileName, exception.getMessage());
    }

}
