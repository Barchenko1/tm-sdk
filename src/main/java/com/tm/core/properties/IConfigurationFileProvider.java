package com.tm.core.properties;

import java.util.Properties;

public interface IConfigurationFileProvider {
    Properties loadHibernateConfigurationFile(String name);
}
