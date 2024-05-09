package com.tm.core.util.setting;

public interface HikariSetting {
    // HikariCP settings

    // Maximum waiting time for a connection from the pool
    String HIBERNATE_HIKARI_CONNECTION_TIMEOUT = "hibernate.hikari.connectionTimeout";
    // Minimum number of ideal connections in the pool
    String HIBERNATE_HIKARI_MINIMUM_IDLE = "hibernate.hikari.minimumIdle";
    // Maximum number of actual connection in the pool
    String HIBERNATE_HIKARI_MAXIMUM_PULL_SIZE = "hibernate.hikari.maximumPoolSize";
    // Maximum time that a connection is allowed to sit ideal in the pool
    String HIBERNATE_HIKARI_IDLE_TIMEOUT = "hibernate.hikari.idleTimeout";

}
