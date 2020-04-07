package com.widget.service.service;

import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;
import com.widget.service.repository.WidgetRepository;

import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Service for working with widgets.
 */
@Service
public class WidgetService {
    private WidgetRepository widgetRepository;

    public WidgetService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    /**
     * Returns all widgets from storage.
     */
    public Page<Widget> getAllWidgets(Pageable pageable, WidgetFilter widgetFilter) {
        if (widgetFilter == null) {
            return widgetRepository.findAll(pageable);
        }
        else {
            return widgetRepository.findByArea(
                widgetFilter.getX1(),
                widgetFilter.getY1(),
                widgetFilter.getX2(),
                widgetFilter.getY2(),
                pageable);
        }
    }

    /**
     * Returns widget from storage by its id.
     * @param id Widget id.
     * @return Widget by its id.
     */
    public Widget getWidgetById(long id) {
        return widgetRepository.findById(id).orElse(null);
    }

    /**
     * Creates and saves widget in storage.
     * @param newWidget Widget to create in storage.
     * @return Saved widget.
     */
    @Transactional
    public Widget createWidget(Widget newWidget) {
        newWidget.setLastModified(ZonedDateTime.now());

        return this.saveWidgetAndUpdateZIndices(newWidget);
    }

    /**
     * Modifies widget.
     * @param updatedWidget Updated entity.
     * @return Saved entity.
     */
    @Transactional
    public Widget modifyWidget(Widget updatedWidget) throws ObjectNotFoundException {
        long widgetId = updatedWidget.getId();
        if (!widgetRepository.existsById(widgetId)) {
            throw new ObjectNotFoundException(widgetId, "Widget");
        }

        updatedWidget.setLastModified(ZonedDateTime.now());

        return this.saveWidgetAndUpdateZIndices(updatedWidget);
    }

    /**
     * Deletes widget from storage by its id.
     * @param id Widget id.
     */
    public void deleteWidget(long id) {
        widgetRepository.deleteById(id);
    }

    /**
     * Saves widget to storage and leads all widgets z-indices to a consistent state.
     * @param widget Widget to save.
     */
    private synchronized Widget saveWidgetAndUpdateZIndices(Widget widget) {
        if (widget.getZ() == null) {
            Integer maxIndex = widgetRepository.getMaxZIndex();
            widget.setZ(maxIndex == null ? 0 : maxIndex + 1);
        }
        else if (widgetRepository.getWidgetsCountByZIndex(widget.getZ()) > 0) {
            widgetRepository.incZIndices(widget.getZ());
        }

        return widgetRepository.save(widget);
    }
}
