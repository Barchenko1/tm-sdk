package com.tm.core.properties;

import java.util.Properties;

public interface IConfigurationFileProvider {
    Properties loadPropertiesByName(String name);
    Properties loadConfigByName(String name);
}
