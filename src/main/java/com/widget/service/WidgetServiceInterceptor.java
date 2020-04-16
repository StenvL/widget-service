package com.widget.service;

import com.widget.service.model.Widget;
import com.widget.storage.abstraction.BaseEntity;
import com.widget.storage.abstraction.IInterceptor;
import com.widget.storage.abstraction.IStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class WidgetServiceInterceptor implements IInterceptor {
    @Autowired
    IStorage storage;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeCreate(BaseEntity entity) {
        if (entity.getClass() == Widget.class) {
            Widget widget = (Widget) entity;
            widget.setLastModified(ZonedDateTime.now());

            if (widget.getZ() == null) {
                Integer maxIndex = getWidgetsMaxZIndex();
                widget.setZ(maxIndex == null ? 0 : maxIndex + 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCreate(BaseEntity entity) {
        if (entity.getClass() == Widget.class) {
            Widget widget = (Widget) entity;

            if (getWidgetsWithSameZIndex(widget).size() > 0) {
                incEqAndGreaterZIndices(widget);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeUpdate(BaseEntity entity) {
        if (entity.getClass() == Widget.class) {
            Widget widget = (Widget) entity;
            widget.setLastModified(ZonedDateTime.now());

            if (widget.getZ() == null) {
                Integer maxIndex = getWidgetsMaxZIndex();
                widget.setZ(maxIndex == null ? 0 : maxIndex + 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate(BaseEntity entity) {
        if (entity.getClass() == Widget.class) {
            Widget widget = (Widget) entity;

            if (getWidgetsWithSameZIndex(widget).size() > 0) {
                incEqAndGreaterZIndices(widget);
            }
        }
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

    /**
     * Returns max z-index for all widgets.
     */
    private Integer getWidgetsMaxZIndex() {
        List<Widget> widgets = storage.findAll(Widget.class, null);
        return widgets
                .stream()
                .map(x -> x.getZ())
                .max(Integer::compare)
                .orElse(null);
    }

    /**
     * Returns widgets with the same z-index.
     * @param widget Widget with z-index to check.
     */
    private List<Widget> getWidgetsWithSameZIndex(Widget widget) {
        return storage.findAll(
                Widget.class,
                x -> x.getZ().equals(widget.getZ()) && !x.getId().equals(widget.getId()),
                null);
    }

    /**
     * Increments z-indices for all widgets that have greater z-index than z-index of passed widget.
     * @param widget Widget.
     */
    private void incEqAndGreaterZIndices(Widget widget) {
        List<Widget> widgets = storage.findAll(
                Widget.class,
                x -> x.getZ().equals(widget.getZ()) && !x.getId().equals(widget.getId()),
                null);
        widgets.forEach(x -> x.setZ(x.getZ() + 1));
        storage.saveAll(widgets);
    }
}
