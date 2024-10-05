package com.tm.core.util.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigurationFileProvider implements IConfigurationFileProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationFileProvider.class);

    @Override
    public Properties loadHibernateConfigurationFile(String fileName) {
        Path filePath = Path.of(fileName);
        if (Files.exists(filePath)) {
            try (InputStream input = Files.newInputStream(filePath)) {
                Properties properties = new Properties();
                properties.load(input);
                return properties;
            } catch (IOException ex) {
                throw new RuntimeException("Error loading properties file: " + ex.getMessage(), ex);
            }
        } else {
            throw new RuntimeException("File not found: " + fileName);
        }
    }
}
