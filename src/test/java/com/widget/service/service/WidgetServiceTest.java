package com.widget.service.service;

import com.widget.service.model.Widget;
import com.widget.service.model.WidgetFilter;
import com.widget.service.repository.WidgetRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.Collection;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class WidgetServiceTest {
    @Autowired
    private WidgetRepository widgetRepository;

    @Autowired
    private WidgetService widgetService;

    @Test
    void whenGetAllWidgets_thenReturnListOfWidgets() {
        // given
        widgetRepository.deleteAll();
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetRepository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setX(25);
        widget2.setY(25);
        widget2.setWidth(5);
        widget2.setHeight(5);
        widgetRepository.save(widget2);

        WidgetFilter filter1 = new WidgetFilter(0, 0, 15, 15);
        WidgetFilter filter2 = new WidgetFilter(20, 20, 35, 35);
        WidgetFilter filter3 = new WidgetFilter(10, 10, 25, 25);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Iterable<Widget> res1 = widgetService.getAllWidgets(pageRequest, filter1);
        Iterable<Widget> res2 = widgetService.getAllWidgets(pageRequest, filter2);
        Iterable<Widget> res3 = widgetService.getAllWidgets(pageRequest, filter3);
        Iterable<Widget> res4 = widgetService.getAllWidgets(pageRequest, null);

        // then
        assertEquals(StreamSupport.stream(res1.spliterator(), false).count(), 1);
        assertEquals(StreamSupport.stream(res2.spliterator(), false).count(), 1);
        assertEquals(StreamSupport.stream(res3.spliterator(), false).count(), 0);
        assertEquals(StreamSupport.stream(res4.spliterator(), false).count(), 2);
    }

    @Test
    void whenGetWidgetById_thenReturnWidget() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetRepository.save(widget1);

        long id = widget1.getId();

        // when
        Widget loadedWidget = widgetService.getWidgetById(id);

        // then
        assertEquals(loadedWidget.getId(), id);
    }

    @Test
    void whenCreateWidget_thenWidgetExistsInRepository() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetService.createWidget((widget1));

        long id = widget1.getId();

        // when
        boolean isExists = widgetRepository.existsById(id);

        // then
        assertTrue(isExists);
    }

    @Test
    void whenCreateWidgetWithExistentZIndex_thenItBecomesUnique() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setZ(1);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetService.createWidget((widget1));
        long widget1Id = widget1.getId();

        Widget widget2 = new Widget();
        widget2.setX(10);
        widget2.setY(10);
        widget2.setZ(1);
        widget2.setWidth(5);
        widget2.setHeight(5);

        // when
        widgetService.createWidget(widget2);

        // then
        assertTrue(widgetRepository.findById(widget1Id).get().getZ() == 2 && widget2.getZ() == 1);
    }

    @Test
    void whenModifyWidget_thenItUpdates() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetRepository.save((widget1));

        long id = widget1.getId();

        // when
        Widget widgetBeforeUpdate = widgetRepository.findById(id).get();
        int xBeforeUpdate = widgetBeforeUpdate.getX();

        widgetBeforeUpdate.setX(20);
        widgetService.modifyWidget(widget1);
        Widget widgetAfterUpdate = widgetRepository.findById(id).get();
        int xAfterUpdate = widgetBeforeUpdate.getX();

        // then
        assertTrue(xBeforeUpdate == 10 && xAfterUpdate == 20);
    }

    @Test
    void whenModifyNonexistentWidget_thenThrows() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetRepository.save((widget1));

        // when
        widgetRepository.delete(widget1);
        widget1.setX(20);

        // then
        assertThrows(ObjectNotFoundException.class, () -> {
            widgetService.modifyWidget(widget1);
        });
    }

    @Test
    void deleteWidget() {
        // given
        Widget widget1 = new Widget();
        widget1.setX(10);
        widget1.setY(10);
        widget1.setWidth(5);
        widget1.setHeight(5);
        widgetRepository.save((widget1));
        long id = widget1.getId();

        assertTrue(widgetRepository.existsById(id));

        // when
        widgetService.deleteWidget(id);

        // then
        assertFalse(widgetRepository.existsById(id));
    }
}