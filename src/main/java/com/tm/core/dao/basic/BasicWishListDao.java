package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.wishlist.IWishListDao;
import org.hibernate.SessionFactory;

public class BasicWishListDao extends AbstractDao implements IWishListDao {
    public BasicWishListDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
