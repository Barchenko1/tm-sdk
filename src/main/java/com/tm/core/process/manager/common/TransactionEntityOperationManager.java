package com.tm.core.process.manager.common;

import com.tm.core.process.dao.common.ITransactionEntityDao;

public class TransactionEntityOperationManager extends AbstractTransactionEntityOperationManager implements IEntityOperationManager {

    public TransactionEntityOperationManager(ITransactionEntityDao transactionEntityDao) {
        super(transactionEntityDao);
    }
}
