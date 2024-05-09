package com.tm.core.util;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tm.core.constant.Constant.POSTGRES_DB_URL;
import static com.tm.core.constant.Constant.POSTGRES_DIALECT;
import static com.tm.core.constant.Constant.POSTGRES_DRIVER;
import static com.tm.core.constant.Constant.POSTGRES_PASSWORD;
import static com.tm.core.constant.Constant.POSTGRES_USERNAME;

public final class DataSourcePool {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourcePool.class);

    public static HikariDataSource getHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(POSTGRES_DB_URL);
        dataSource.setUsername(POSTGRES_USERNAME);
        dataSource.setPassword(POSTGRES_PASSWORD);
        dataSource.setDriverClassName(POSTGRES_DRIVER);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        return dataSource;
    }

}
