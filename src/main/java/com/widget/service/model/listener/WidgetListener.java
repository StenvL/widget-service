package com.widget.service.model.listener;

import com.widget.service.model.Widget;
import com.widget.service.service.WidgetService;
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