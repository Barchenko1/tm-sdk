package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.option.IOptionGroupDao;
import org.hibernate.SessionFactory;

public class BasicOptionGroupDao extends AbstractDao implements IOptionGroupDao {
    public BasicOptionGroupDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
