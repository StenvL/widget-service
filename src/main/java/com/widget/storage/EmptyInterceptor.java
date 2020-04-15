package com.widget.storage;

import com.widget.storage.abstraction.BaseEntity;
import com.widget.storage.abstraction.IInterceptor;

public class EmptyInterceptor implements IInterceptor {
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeCreate(BaseEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCreate(BaseEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeUpdate(BaseEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate(BaseEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeDelete(BaseEntity entity) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterDelete(BaseEntity entity) {

    }
}
