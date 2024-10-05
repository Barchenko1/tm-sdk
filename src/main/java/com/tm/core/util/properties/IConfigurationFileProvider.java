package com.tm.core.util.properties;

import java.util.Properties;

public interface IConfigurationFileProvider {
    Properties loadHibernateConfigurationFile(String name);
}
