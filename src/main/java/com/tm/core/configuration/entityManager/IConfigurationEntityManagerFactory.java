package com.tm.core.configuration.entityManager;

import jakarta.persistence.EntityManagerFactory;

public interface IConfigurationEntityManagerFactory {

    EntityManagerFactory configureEntityManagerFactory();
}
