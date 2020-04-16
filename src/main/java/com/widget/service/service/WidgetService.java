package com.widget.service.service;

import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;

import com.widget.storage.EntityNotFoundException;
import com.widget.storage.abstraction.IStorage;
import com.widget.storage.contract.PageRequest;
import com.widget.storage.contract.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Comparator<Widget> zSortComparator = Comparator.comparing(Widget::getZ, Comparator.reverseOrder());
        if (widgetFilter == null) {
            return storage.findAll(Widget.class, pageRequest, zSortComparator);
        }
        else {
            Comparator<Widget> areaSortComparator = Comparator.comparing(x -> x.getWidth() * x.getHeight());
            List<Widget> allWidgets = storage.findAll(Widget.class, areaSortComparator);

            List filteredWidgets = filterByArea(allWidgets, widgetFilter);
            return PageResponse.create(filteredWidgets, pageRequest);
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

    /**
     * Filters collection of widgets by area.
     * @param widgets Widgets collection.
     * @param filter Widgets filter.
     */
    private List<Widget> filterByArea(List<Widget> widgets, WidgetFilter filter) {
        List<Widget> result = new ArrayList<>();

        boolean isWidgetAreaOver = false;
        double filterArea = (filter.getX2() - filter.getX1()) * (filter.getY2() - filter.getY1());

        Iterator<Widget> iterator = widgets.iterator();
        while(iterator.hasNext() && !isWidgetAreaOver) {
            Widget curWidget = iterator.next();

            double widgetArea = curWidget.getWidth() * curWidget.getHeight();
            if (widgetArea <= filterArea) {
                if (curWidget.getX() - curWidget.getWidth() / 2 >= filter.getX1()
                        && curWidget.getX() + curWidget.getWidth() / 2 <= filter.getX2()
                        && curWidget.getY() - curWidget.getHeight() / 2 >= filter.getY1()
                        && curWidget.getY() + curWidget.getHeight() / 2<= filter.getY2()) {
                    result.add(curWidget);
                }
            } else {
                isWidgetAreaOver = true;
            }
        }

        return result;
    }
}
