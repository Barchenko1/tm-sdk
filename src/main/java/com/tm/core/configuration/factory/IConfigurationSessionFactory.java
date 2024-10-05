package com.tm.core.configuration.factory;

import org.hibernate.SessionFactory;

public interface IConfigurationSessionFactory {

    SessionFactory configureSessionFactory();
}
