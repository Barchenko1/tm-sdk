package com.tm.core.process.manager.common.impl;

import com.tm.core.process.dao.common.ITransactionEntityDao;

public class TransactionEntityOperationManager extends AbstractTransactionEntityOperationManager {

    public TransactionEntityOperationManager(ITransactionEntityDao transactionEntityDao) {
        super(transactionEntityDao);
    }
}
