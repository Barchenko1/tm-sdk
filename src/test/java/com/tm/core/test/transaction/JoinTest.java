package com.tm.core.test.transaction;

import com.tm.core.config.ConfigDbType;
import com.tm.core.config.factory.ConfigurationSessionFactory;
import com.tm.core.dto.BasicDtoEntityDao;
import com.tm.core.dto.IDtoEntityDao;
import com.tm.core.test.base.AbstractJoinTest;
import com.tm.core.test.modal.EmployeeDependentTestDto;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tm.core.util.DataSourcePool.getHikariDataSource;

public class JoinTest extends AbstractJoinTest {
    private static ConnectionHolder connectionHolder;
    private static IDtoEntityDao dtoEntityDao;

    private static final String SQL_QUERY = """
            SELECT te.id AS employeeId, te.name AS employeeName, td.id AS dependentId, td.name AS dependentName, td.status AS dependentStatus
            FROM TestEmployee te
            LEFT JOIN TestDependent td ON te.id = td.testEmployee_id;
            """;

    private static final String SQL_QUERY_WHERE = """
            SELECT te.id AS employeeId, te.name AS employeeName, td.id AS dependentId, td.name AS dependentName, td.status AS dependentStatus
            FROM TestEmployee te
            LEFT JOIN TestDependent td ON te.id = td.testEmployee_id where td.id = '%s';
            """;

    @BeforeAll
    public static void getSessionFactory() {
        ConfigurationSessionFactory configurationSessionFactory = new ConfigurationSessionFactory(
                ConfigDbType.XML
        );
        sessionFactory = configurationSessionFactory.getSessionFactory();
        dtoEntityDao = new BasicDtoEntityDao(sessionFactory);
        dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;
    }

    @BeforeEach
    public void BeforeEach() {
        prepareTestEntityDb();
    }

    @Test
    @ExpectedDataSet(value = "/data/dataset/initDataJoinSet.xml")
    void dtoEntityDaoSingleTest() {
        String sqlQuery = String.format(SQL_QUERY_WHERE, 1);
        EmployeeDependentTestDto employeeDependentTestDto = dtoEntityDao.getDto(sqlQuery, EmployeeDependentTestDto.class);
        Assertions.assertEquals(1, employeeDependentTestDto.getEmployeeId());
    }

    @Test
    @ExpectedDataSet(value = "/data/dataset/initDataJoinSet.xml")
    void dtoEntityDaoListTest() {
        List<EmployeeDependentTestDto> employeeDependentTestDtoList = dtoEntityDao.getDtoList(SQL_QUERY, EmployeeDependentTestDto.class);
        Assertions.assertFalse(employeeDependentTestDtoList.isEmpty());
    }

}
