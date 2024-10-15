package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.single.SingleTestEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.table.EntityTable;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
public class EntityIdentifierDaoTest {

    private static IEntityIdentifierDao entityIdentifierDao;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        SessionFactory sessionFactory = getSessionFactory();
        IThreadLocalSessionManager sessionManager = new ThreadLocalSessionManager(sessionFactory);
        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        EntityTable entityTable = new EntityTable(SingleTestEntity.class, "singleTestEntity");
        entityMappingManager.addEntityTable(entityTable);
        entityIdentifierDao = new EntityIdentifierDao(sessionManager, entityMappingManager);
    }



    @Test
    @DataSet("/datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetEntityList() {
        List<SingleTestEntity> entities =
                entityIdentifierDao.getEntityList(SingleTestEntity.class, new Parameter("id", 1));

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(1, entities.size());
    }

    @Test
    @DataSet("/datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetEntity() {
        SingleTestEntity entity =
                entityIdentifierDao.getEntity(SingleTestEntity.class, new Parameter("id", 1));

        assertNotNull(entity);
        assertEquals(1, entity.getId());
    }

    @Test
    @DataSet("datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetOptionalEntity() {
        Optional<SingleTestEntity> optionalEntity =
                entityIdentifierDao.getOptionalEntity(SingleTestEntity.class, new Parameter("id", 1));

        assertTrue(optionalEntity.isPresent());
        assertEquals(1, optionalEntity.get().getId());
    }

    @Test
    @DataSet("datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetListEntity() {
        List<SingleTestEntity> entities =
                entityIdentifierDao.getEntityList(SingleTestEntity.class, new Parameter("id", 1));

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(1, entities.size());
    }

    @Test
    @DataSet("datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetListEntityWithMultiOneTypeParams() {
        List<SingleTestEntity> entities =
                entityIdentifierDao.getEntityList(SingleTestEntity.class, new Parameter[] {
                        new Parameter("id", 1),
                        new Parameter("id", 2),
                });

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(2, entities.size());
        assertEquals(1 , entities.get(0).getId());
        assertEquals(2 , entities.get(1).getId());
    }

    @Test
    @DataSet("datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetListEntityWithMultiDifferentTypeParams() {
        List<SingleTestEntity> entities =
                entityIdentifierDao.getEntityList(SingleTestEntity.class, new Parameter[] {
                        new Parameter("id", 1),
                        new Parameter("name", "Test Entity 3"),
                });

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(2, entities.size());
        assertEquals(1 , entities.get(0).getId());
        assertEquals(3 , entities.get(1).getId());
    }

    @Test
    @DataSet("datasets/single/searchTestSingleEntityDataSet.yml")
    public void testGetEntityListWithNullParameters() {
        List<SingleTestEntity> entities = entityIdentifierDao.getEntityList(SingleTestEntity.class);

        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(3, entities.size());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    public void testGetOptionalEntityWithNullParameters() {
        Optional<SingleTestEntity> optionalEntity = entityIdentifierDao.getOptionalEntity(SingleTestEntity.class, (Parameter[]) null);

        assertFalse(optionalEntity.isEmpty());
    }

}
