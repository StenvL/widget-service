package com.widget.service.controller;

import com.widget.storage.contract.PageRequest;
import com.widget.service.contract.WidgetQuery;
import com.widget.service.contract.WidgetRequest;
import com.widget.service.contract.WidgetResponse;
import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;
import com.widget.service.service.WidgetService;
import com.widget.storage.EntityNotFoundException;
import com.widget.storage.contract.PageResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/widgets")
public class WidgetController {
    private ModelMapper mapper;
    private WidgetService widgetService;

    @Autowired
    public WidgetController(WidgetService widgetService, ModelMapper mapper) {
        this.widgetService = widgetService;
        this.mapper = mapper;
    }

    /**
     * Handles GET request to /widgets.
     * @return All widgets.
     */
    @GetMapping
    public ResponseEntity<?> getAll(@Valid PageRequest pageRequest, WidgetQuery widgetQuery) {
        PageResponse<Widget> pagedWidgets = null;
        WidgetFilter widgetFilter = null;

        if (!widgetQuery.isEmpty()) {
            if (widgetQuery.isValid()) {
                widgetFilter = mapper.map(widgetQuery, WidgetFilter.class);
                pagedWidgets = widgetService.getAllWidgets(pageRequest, widgetFilter);
            }
            else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
        else {
            pagedWidgets = widgetService.getAllWidgets(pageRequest, null);
        }

        Type targetListType = new TypeToken<List<WidgetResponse>>() {}.getType();
        List<WidgetResponse> result = mapper.map(pagedWidgets.getRecords(), targetListType);

        return ResponseEntity.ok(result);
    }

    /**
     * Handles GET request to /widgets/{id}.
     * @param id Identifier of widget.
     * @return Widget by its identifier.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") UUID id) {
        Widget widget = widgetService.getWidgetById(id);
        if (widget == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(mapper.map(widget, WidgetResponse.class));
    }

    /**
     * Handles POST request to /widgets
     * @param widgetRequest Request body for widget to create.
     * @return Created widget.
     */
    @PostMapping("/")
    public ResponseEntity<?> post(@RequestBody @Valid WidgetRequest widgetRequest) {
        if (widgetRequest == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Widget widget = mapper.map(widgetRequest, Widget.class);
        widget = widgetService.createWidget(widget);

        return new ResponseEntity(mapper.map(widget, WidgetResponse.class), HttpStatus.CREATED);
    }

    /**
     * Handles PUT request to /widgets/{id}.
     * @param widgetRequest Request body for widget to update.
     * @param id Identifier of widget.
     * @return Updated widget.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@RequestBody @Valid WidgetRequest widgetRequest, @PathVariable UUID id) {
        if (widgetRequest == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            Widget widget = mapper.map(widgetRequest, Widget.class);
            widget.setId(id);
            widget = widgetService.modifyWidget(widget);

            return ResponseEntity.ok(mapper.map(widget, WidgetResponse.class));
        }
        catch(EntityNotFoundException ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Handles DELETE request to /widgets/{id}.
     * @param id Identifier of widget.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            widgetService.deleteWidget(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch(EntityNotFoundException ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
