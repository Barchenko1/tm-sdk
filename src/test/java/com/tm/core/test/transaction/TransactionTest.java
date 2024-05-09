package com.tm.core.test.transaction;

import com.tm.core.config.ConfigDbType;
import com.tm.core.config.factory.ConfigurationSessionFactory;
import com.tm.core.modal.TestDependent;
import com.tm.core.modal.TestEmployee;
import com.tm.core.test.base.AbstractTransactionTest;
import com.tm.core.transaction.BasicTransactionManager;
import com.tm.core.transaction.ITransactionManager;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static com.tm.core.util.DataSourcePool.getHikariDataSource;

@ExtendWith(DBUnitExtension.class)
public class TransactionTest extends AbstractTransactionTest {
    private static ConnectionHolder connectionHolder;
    private static ITransactionManager transactionManager;

    @BeforeAll
    public static void getSessionFactory() {
        ConfigurationSessionFactory configurationSessionFactory = new ConfigurationSessionFactory(
                ConfigDbType.XML
        );
        sessionFactory = configurationSessionFactory.getSessionFactory();
        transactionManager = new BasicTransactionManager(sessionFactory);

        dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;
    }

    @BeforeEach
    public void BeforeEach() {
        prepareTestEntityDb();
    }

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true)
    @ExpectedDataSet(value = "/data/expected/createExpectedTransactionSet.xml")
    void saveCorrectTransactionTest() {
        TestDependent dependent1 = setUpDependent("Liza", "GirlFriend");
        TestDependent dependent2 = setUpDependent("Bread", "Child");

        List<TestDependent> dependents = new ArrayList<>();
        dependents.add(dependent1);
        dependents.add(dependent2);

        TestEmployee employee = new TestEmployee();
        employee.setName("Robert");
        employee.setTestDependents(dependents);

        transactionManager.useTransaction(employee);
    }

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true)
    void saveIncorrectTransactionTest() {
        TestDependent dependent1 = setUpDependent("Liza", "GirlFriend");
        TestDependent dependent2 = setUpDependent("Bread", "Child");

        List<TestDependent> dependents = new ArrayList<>();
        dependents.add(dependent1);
        dependents.add(dependent2);

        TestEmployee employee = new TestEmployee();
        employee.setName("Robert");

        Assertions.assertThrows(RuntimeException.class, () -> {
            transactionManager.useTransaction(null);
        });
    }

    private TestDependent setUpDependent(String name, String status) {
        TestDependent dependent = new TestDependent();
        dependent.setName(name);
        dependent.setStatus(status);
        return dependent;
    }

}
