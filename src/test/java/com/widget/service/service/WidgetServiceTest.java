package com.widget.service.service;

import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;
import com.widget.storage.EntityNotFoundException;
import com.widget.storage.abstraction.IStorage;
import com.widget.storage.contract.PageRequest;
import com.widget.storage.contract.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class WidgetServiceTest {
    @Autowired
    private IStorage storage;

    @Autowired
    private WidgetService widgetService;

    @BeforeEach
    public void init() {
        storage.deleteAll(Widget.class);
    }

    @Test
    void getAllWidgets_WithPaging_ReturnWidgetsPage() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();
        Widget widget3 = getWidget();
        widget1.setZ(3);
        widget2.setZ(2);
        widget3.setZ(1);

        storage.saveAll(new ArrayList<>(List.of(widget1, widget2, widget3)));

        // when
        PageResponse widgets = widgetService.getAllWidgets(new PageRequest(1, 2), null);

        // then
        int[] expected = { 1 };
        assertArrayEquals(
                widgets.getRecords().stream().mapToInt(x -> ((Widget)x).getZ()).toArray(),
                expected);
    }


    @Test
    void getAllWidgets_WithPagingAndFiltering_ReturnsFilteredWidgetsPage() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetService.createWidget(widget1);

        Widget widget2 = new Widget();
        widget2.setX(25);
        widget2.setY(25);
        widget2.setWidth(6);
        widget2.setHeight(6);
        widgetService.createWidget(widget2);

        WidgetFilter filter1 = new WidgetFilter(0., 0., 15., 15.);
        WidgetFilter filter2 = new WidgetFilter(20., 20., 35., 35.);
        WidgetFilter filter3 = new WidgetFilter(10., 10., 25., 25.);
        WidgetFilter filter4 = new WidgetFilter(7., 7., 12.5, 12.5);
        PageRequest pageRequest = new PageRequest(0, 10);

        // when
        PageResponse res1 = widgetService.getAllWidgets(pageRequest, filter1);
        PageResponse res2 = widgetService.getAllWidgets(pageRequest, filter2);
        PageResponse res3 = widgetService.getAllWidgets(pageRequest, filter3);
        PageResponse res4 = widgetService.getAllWidgets(pageRequest, filter4);
        PageResponse res5 = widgetService.getAllWidgets(pageRequest, null);

        // then
        assertEquals(res1.getRecords().size(), 1);
        assertEquals(res2.getRecords().size(), 1);
        assertEquals(res3.getRecords().size(), 0);
        assertEquals(res4.getRecords().size(), 1);
        assertEquals(res5.getRecords().size(), 2);
    }

    @Test
    void getWidgetById_ExistingWidget_ReturnsWidget() {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);
        UUID id = widgetToSave.getId();

        // when
        Widget loadedWidget = widgetService.getWidgetById(id);

        // then
        assertEquals(loadedWidget.getId(), id);
    }

    @Test
    void createWidget_ItExistsInRepository() {
        // given
        Widget widgetToCreate = getWidget();
        widgetService.createWidget(widgetToCreate);
        UUID id = widgetToCreate.getId();

        // when
        boolean isExists = storage.exists(Widget.class, id);

        // then
        assertTrue(isExists);
    }

    @Test
    void createWidget_ExistentZIndex_ItBecomesUnique() {
        // given
        Widget widget1 = getWidget();
        widget1.setZ(1);
        widgetService.createWidget(widget1);
        UUID widget1Id = widget1.getId();

        Widget widget2 = getWidget();
        widget2.setZ(1);

        // when
        widgetService.createWidget(widget2);

        // then
        assertTrue(storage.findById(Widget.class, widget1Id).get().getZ() == 2 && widget2.getZ() == 1);
    }

    @Test
    void modifyWidget_ItUpdates() throws EntityNotFoundException {
        // given
        Widget widgetToCreate = getWidget();
        widgetService.createWidget(widgetToCreate);
        UUID id = widgetToCreate.getId();

        // when
        Widget widgetBeforeUpdate = storage.findById(Widget.class, id).get();
        assertEquals(widgetBeforeUpdate.getX(), 10);

        widgetBeforeUpdate.setX(20);
        widgetService.modifyWidget(widgetBeforeUpdate);
        int xAfterUpdate = storage.findById(Widget.class, id).get().getX();

        // then
        assertEquals(xAfterUpdate, 20);
    }

    @Test
    void modifyWidget_NonExistentWidget_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);
        assertTrue(storage.exists(Widget.class, widgetToSave.getId()));

        // when
        storage.delete(widgetToSave);
        widgetToSave.setX(20);

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            widgetService.modifyWidget(widgetToSave);
        });
    }

    @Test
    void deleteWidget_ItDisappeared() throws EntityNotFoundException {
        // given
        Widget widget1 = getWidget();
        storage.save(widget1);
        UUID id = widget1.getId();

        assertTrue(storage.exists(Widget.class, id));

        // when
        widgetService.deleteWidget(id);

        // then
        assertFalse(storage.exists(Widget.class, id));
    }


    private Widget getWidget() {
        Widget widget = new Widget();
        widget.setX(10);
        widget.setY(10);
        widget.setWidth(5);
        widget.setHeight(5);

        return widget;
    }
}