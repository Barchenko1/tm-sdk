package com.tm.core.configuration.cp;

import org.hibernate.SessionFactory;

public interface IConnectionPullConfiguration {

    SessionFactory createSessionFactoryByProperties(String filename);
    SessionFactory createSessionFactoryByHibernateXML(String filename);
    void setAnnotatedClasses(Class<?>[] classes);
}
