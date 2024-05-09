package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.option.IOptionDao;
import org.hibernate.SessionFactory;

public class BasicOptionDao extends AbstractDao implements IOptionDao {
    public BasicOptionDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
