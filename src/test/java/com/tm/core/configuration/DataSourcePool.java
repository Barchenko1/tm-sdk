package com.tm.core.configuration;

import com.zaxxer.hikari.HikariDataSource;

import static com.tm.core.constant.Constant.POSTGRES_DB_URL;
import static com.tm.core.constant.Constant.POSTGRES_DRIVER;
import static com.tm.core.constant.Constant.POSTGRES_PASSWORD;
import static com.tm.core.constant.Constant.POSTGRES_USERNAME;

public final class DataSourcePool {
    public static HikariDataSource getHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(POSTGRES_DB_URL);
        dataSource.setUsername(POSTGRES_USERNAME);
        dataSource.setPassword(POSTGRES_PASSWORD);
        dataSource.setDriverClassName(POSTGRES_DRIVER);
        dataSource.setMaximumPoolSize(100);
        dataSource.setMinimumIdle(10);
        dataSource.setConnectionTimeout(10000);

        return dataSource;
    }

}
