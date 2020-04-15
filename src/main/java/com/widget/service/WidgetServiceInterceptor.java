package com.widget.service;

import com.widget.service.model.Widget;
import com.widget.storage.abstraction.BaseEntity;
import com.widget.storage.abstraction.IInterceptor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class WidgetServiceInterceptor implements IInterceptor {
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeCreate(BaseEntity entity) {
        if (entity.getClass() == Widget.class) {
            ((Widget) entity).setLastModified(ZonedDateTime.now());
        }
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
        if (entity.getClass() == Widget.class) {
            ((Widget) entity).setLastModified(ZonedDateTime.now());
        }
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
