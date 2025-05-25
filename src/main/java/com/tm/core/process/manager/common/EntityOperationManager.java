package com.tm.core.process.manager.common;

import com.tm.core.process.dao.common.ITransactionEntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityOperationManager extends AbstractEntityOperationManager implements IEntityOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityOperationManager.class);

    public EntityOperationManager(ITransactionEntityDao dao) {
        super(dao);
    }
}
