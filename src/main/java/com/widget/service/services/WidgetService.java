package com.widget.service.services;

import com.widget.service.models.Widget;
import com.widget.service.models.WidgetsRepository;

import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for working with widgets.
 */
@Service
public class WidgetService {
    private WidgetsRepository widgetsRepository;

    public WidgetService(WidgetsRepository widgetsRepository) {
        this.widgetsRepository = widgetsRepository;
    }

    /**
     * Returns all widgets from storage.
     */
    public Iterable<Widget> getAllWidgets() {
        return widgetsRepository.findAll();
    }

    /**
     * Returns widget from storage by its id.
     * @param id Widget id.
     * @return Widget by its id.
     */
    public Widget getWidgetById(long id) {
        return widgetsRepository.findById(id).orElse(null);
    }

    /**
     * Creates and saves widget in storage.
     * @param newWidget Widget to create in storage.
     * @return Saved widget.
     */
    public Widget createWidget(Widget newWidget) {
        return widgetsRepository.save(newWidget);
    }

    /**
     * Modifies widget.
     * @param updatedWidget Updated entity.
     * @return Saved entity.
     */
    public Widget modifyWidget(Widget updatedWidget) throws ObjectNotFoundException {
        long widgetId = updatedWidget.getId();
        if (!widgetsRepository.existsById(widgetId)) {
            throw new ObjectNotFoundException(widgetId, "Widget");
        }

        return widgetsRepository.save(updatedWidget);
    }

    /**
     * Deletes widget from storage by its id.
     * @param id Widget id.
     */
    public void deleteWidget(long id) {
        widgetsRepository.deleteById(id);
    }

    /**
     * Leads all widgets z-indices to a consistent state when creating new one.
     * @param widget Widget to create.
     */
    public void updateZIndices(Widget widget) {
        if (widget.getZ() == null) {
            Integer maxIndex = widgetsRepository.getMaxZIndex();
            widget.setZ(maxIndex == null ? 0 : maxIndex + 1);
        }
        else if (widgetsRepository.getWidgetsCountByZIndex(widget.getZ()) > 0) {
            widgetsRepository.incZIndices(widget.getZ());
        }
    }
}
