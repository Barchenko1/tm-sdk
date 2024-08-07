package com.tm.core.test;

import com.tm.core.configuration.factory.ConfigurationSessionFactory;
import com.tm.core.dao.basic.TestEntitySingleEntityDao;
import com.tm.core.modal.TestEntity;
import com.tm.core.processor.ThreadLocalSessionManager;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static com.tm.core.util.DataSourcePool.getHikariDataSource;

@ExtendWith(DBUnitExtension.class)
@DataSet(cleanAfter = true)
public class HikariDaoConfigurationFileTest extends AbstractDaoConfigurationTest {

    private static ConnectionHolder connectionHolder;

    public HikariDaoConfigurationFileTest() {
    }

    @BeforeAll
    public static void getSessionFactory() {
        ConfigurationSessionFactory configurationSessionFactory = new ConfigurationSessionFactory(
                "custom.hikari.hibernate.cfg.xml"
        );
        sessionFactory = configurationSessionFactory.getSessionFactory();
        sessionManager = new ThreadLocalSessionManager(sessionFactory);
        testEntityDao = new TestEntitySingleEntityDao(sessionFactory, TestEntity.class);
        dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

    }

    @BeforeEach
    public void BeforeEach() {
        prepareTestEntityDb();
    }

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true)
    @ExpectedDataSet(value = "/data/expected/createOneExpectedSet.xml")
    void saveDaoTest() {
        TestEntity testEntity = new TestEntity();
        testEntity.setName("testSave");

        testEntityDao.saveEntity(testEntity);
    }

    @Test
    @ExpectedDataSet("/data/expected/updateExpectedSet.xml")
    void updateDaoTest() {
        TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setName("Updated");
        testEntityDao.updateEntity(testEntity);
    }

    @Test
    @ExpectedDataSet(value = "/data/expected/deleteExpectedSet.xml")
    void deleteDaoTest() {
        TestEntity testEntity = new TestEntity();
        testEntity.setId(2L);
        testEntity.setName("Test2");

        testEntityDao.deleteEntity(testEntity);
    }

    @Test
    @ExpectedDataSet(value = "/data/dataset/initDataSet.xml")
    void getTestEntityList() {
        List<TestEntity> resultList = testEntityDao.getAllTestEntities();
        Assertions.assertEquals(2, resultList.size());
    }

    @Test
    void getTestEntity() {
        Optional<TestEntity> result = testEntityDao
                .getTestEntityByUser("Test1");

        Assertions.assertEquals("Test1", result.get().getName());
    }

}

