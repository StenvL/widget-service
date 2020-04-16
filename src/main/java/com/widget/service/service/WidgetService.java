package com.widget.service.service;

import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;

import com.widget.storage.EntityNotFoundException;
import com.widget.storage.abstraction.IStorage;
import com.widget.storage.contract.PageRequest;
import com.widget.storage.contract.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Service for working with widgets.
 */
@Service
public class WidgetService {
    @Autowired
    private IStorage storage;

    /**
     * Returns all widgets from the storage.
     * @param pageRequest Paging request.
     * @param widgetFilter Filter.
     * @return
     */
    public PageResponse getAllWidgets(PageRequest pageRequest, WidgetFilter widgetFilter) {
        Comparator<Widget> sortComparator = Comparator.comparing(Widget::getZ, Comparator.reverseOrder());
        if (widgetFilter == null) {
            return storage.findAll(Widget.class, pageRequest, sortComparator);
        }
        else {
            Predicate<Widget> predicate = x ->
                x.getX() >= widgetFilter.getX1() + x.getWidth() / 2
                && x.getY() >= widgetFilter.getY1() + x.getHeight() / 2
                && x.getX() <= widgetFilter.getX2() - x.getWidth() / 2
                && x.getY() <= widgetFilter.getY2() - x.getHeight() / 2;
            return storage.findAll(Widget.class, predicate, pageRequest, sortComparator);
        }
    }

    /**
     * Returns widget from the storage by its id.
     * @param id Widget id.
     * @return Widget by its id.
     */
    public Widget getWidgetById(UUID id) {
        return storage.findById(Widget.class, id).orElse(null);
    }

    /**
     * Creates and saves widget in storage.
     * @param newWidget Widget to create in storage.
     * @return Saved widget.
     */
    public Widget createWidget(Widget newWidget) {
        return storage.save(newWidget);
    }

    /**
     * Modifies widget.
     * @param updatedWidget Updated widget.
     * @return Saved entity.
     */
    public Widget modifyWidget(Widget updatedWidget) throws EntityNotFoundException {
        UUID widgetId = updatedWidget.getId();
        if (!storage.exists(Widget.class, widgetId)) {
            throw new EntityNotFoundException();
        }

        return storage.save(updatedWidget);
    }

    /**
     * Deletes widget from storage by its id.
     * @param id Widget id.
     */
    public void deleteWidget(UUID id) throws EntityNotFoundException {
        storage.deleteById(Widget.class, id);
    }


    private List<Widget> filterByArea(List<Widget> widgets) {
        return widgets;
    }
}
