package com.tm.core.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationFileProvider implements IConfigurationFileProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFileProvider.class);

    @Override
    public Properties loadPropertiesByName(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/" + fileName)) {
            if (input == null) {
                throw new IOException("File not found: " + fileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file: " + ex.getMessage(), ex);
        }
        return properties;
    }

    @Override
    public Properties loadConfigByName(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/" + fileName)) {
            if (input == null) {
                throw new IOException("File not found: " + fileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file: " + ex.getMessage(), ex);
        }
        return properties;
    }
}
