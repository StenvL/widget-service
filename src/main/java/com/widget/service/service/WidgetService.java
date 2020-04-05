package com.widget.service.service;

import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;
import com.widget.service.repository.WidgetsRepository;

import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

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
     * @return
     */
    public Page<Widget> getAllWidgets(Pageable pageable, WidgetFilter widgetFilter) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("z").descending());

        if (widgetFilter == null) {
            return widgetsRepository.findAll(pageRequest);
        }
        else {
            return widgetsRepository.findByArea(
                widgetFilter.getX1(),
                widgetFilter.getY1(),
                widgetFilter.getX2(),
                widgetFilter.getY2(),
                pageRequest);
        }
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
        newWidget.setLastModified(ZonedDateTime.now());

        return this.saveWidgetAndUpdateZIndices(newWidget);
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

        updatedWidget.setLastModified(ZonedDateTime.now());

        return this.saveWidgetAndUpdateZIndices(updatedWidget);
    }

    /**
     * Deletes widget from storage by its id.
     * @param id Widget id.
     */
    public void deleteWidget(long id) {
        widgetsRepository.deleteById(id);
    }

    /**
     * Saves widget to storage and leads all widgets z-indices to a consistent state.
     * @param widget Widget to save.
     */
    private synchronized Widget saveWidgetAndUpdateZIndices(Widget widget) {
        if (widget.getZ() == null) {
            Integer maxIndex = widgetsRepository.getMaxZIndex();
            widget.setZ(maxIndex == null ? 0 : maxIndex + 1);
        }
        else if (widgetsRepository.getWidgetsCountByZIndex(widget.getZ()) > 0) {
            widgetsRepository.incZIndices(widget.getZ());
        }

        return widgetsRepository.save(widget);
    }
}
