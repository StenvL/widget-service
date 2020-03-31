package com.widget.service.models.listeners;

import com.widget.service.models.Widget;
import com.widget.service.services.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import java.time.ZonedDateTime;

public class WidgetListener {
    private WidgetService widgetService;

    @Autowired
    public WidgetListener(@Lazy WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PrePersist
    public void beforeCreate(Widget widget) {
        widget.setLastModified(ZonedDateTime.now());
        widgetService.updateZIndices(widget);
    }

    @PreUpdate
    public void beforeUpdate(Widget widget) {
        widget.setLastModified(ZonedDateTime.now());
        widgetService.updateZIndices(widget);
    }
}