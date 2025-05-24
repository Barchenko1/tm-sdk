package com.tm.core.process.dao.generic.session;

import com.tm.core.process.dao.generic.IGenericTransactionDao;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.dao.transaction.SessionTransactionHandler;
import org.hibernate.SessionFactory;

public abstract class AbstractGenericTransactionSessionDao extends AbstractGenericSessionDao implements IGenericTransactionDao {

    protected final ITransactionHandler transactionHandler;

    public AbstractGenericTransactionSessionDao(SessionFactory sessionFactory,
                                                String entityPackage) {
        super(sessionFactory, entityPackage);
        this.transactionHandler = new SessionTransactionHandler(sessionFactory);
    }

}
