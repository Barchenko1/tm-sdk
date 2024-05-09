package com.tm.core.dao.basic;


import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.user.IUserAddressDao;
import org.hibernate.SessionFactory;

public class BasicUserAddressDao extends AbstractDao implements IUserAddressDao {
    public BasicUserAddressDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
