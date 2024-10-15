package com.tm.core.configuration;

import com.zaxxer.hikari.HikariDataSource;

import static com.tm.core.constant.Constant.POSTGRES_DB_URL;
import static com.tm.core.constant.Constant.POSTGRES_DRIVER;
import static com.tm.core.constant.Constant.POSTGRES_PASSWORD;
import static com.tm.core.constant.Constant.POSTGRES_USERNAME;

public final class DataSourcePool {

    private static HikariDataSource dataSource;

    private DataSourcePool() {}

    public static HikariDataSource getHikariDataSource() {
        if (dataSource == null) {
            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(POSTGRES_DB_URL);
            dataSource.setUsername(POSTGRES_USERNAME);
            dataSource.setPassword(POSTGRES_PASSWORD);
            dataSource.setDriverClassName(POSTGRES_DRIVER);
            dataSource.setMaximumPoolSize(200);
            dataSource.setMinimumIdle(5);
            dataSource.setConnectionTimeout(10000);
        }

        return dataSource;
    }

}
