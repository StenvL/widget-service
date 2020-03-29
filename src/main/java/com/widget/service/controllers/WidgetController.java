package com.widget.service.controllers;

import com.widget.service.contracts.WidgetRequest;
import com.widget.service.contracts.WidgetResponse;
import com.widget.service.models.Widget;
import com.widget.service.services.WidgetService;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

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
    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        Iterable<Widget> widgets = widgetService.getAllWidgets();

        Type targetListType = new TypeToken<List<WidgetResponse>>() {}.getType();
        List<WidgetResponse> result = mapper.map(widgets, targetListType);

        return ResponseEntity.ok(result);
    }

    /**
     * Handles GET request to /widgets/{id}.
     * @param id Identifier of widget.
     * @return Widget by its identifier.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") long id) {
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
    public ResponseEntity<?> post(@RequestBody WidgetRequest widgetRequest) {
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
    public ResponseEntity<?> put(@RequestBody WidgetRequest widgetRequest, @PathVariable long id) {
        if (widgetRequest == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            Widget widget = mapper.map(widgetRequest, Widget.class);
            widget.setId(id);
            widget = widgetService.modifyWidget(widget);

            return ResponseEntity.ok(mapper.map(widget, WidgetResponse.class));
        }
        catch(ObjectNotFoundException ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Handles DELETE request to /widgets/{id}.
     * @param id Identifier of widget.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            widgetService.deleteWidget(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch(EmptyResultDataAccessException ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
