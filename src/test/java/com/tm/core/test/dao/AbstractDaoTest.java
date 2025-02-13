package com.tm.core.test.dao;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSetExecutor;
import com.github.database.rider.core.dataset.DataSetExecutorImpl;
import com.github.database.rider.junit5.api.DBRider;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;

import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static com.tm.core.test.dao.ConfigureSessionFactoryTest.getSessionFactory;

@ExtendWith(MockitoExtension.class)
@DBRider
public abstract class AbstractDaoTest extends DataBaseLoader {

    protected static ConnectionHolder connectionHolder;
    private static DataSetExecutor executor;
    protected static SessionFactory sessionFactory;

    public AbstractDaoTest() {
        super(connectionHolder, executor);
    }

    @BeforeAll
    public static void setupDatabase() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;
        executor = DataSetExecutorImpl.instance("executor-name", connectionHolder);

        sessionFactory = getSessionFactory();
    }

    @BeforeEach
    public void cleanUpMiddleTable() {
        try (Connection connection = connectionHolder.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM employee_dependent");
            statement.execute("DELETE FROM employee_item");
            statement.execute("DELETE FROM employee");
            statement.execute("DELETE FROM dependent");
            statement.execute("DELETE FROM employee_item");
            statement.execute("DELETE FROM item");
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean table:", e);
        }
    }

}
