package com.widget.service.repository;

import com.widget.service.model.Widget;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class WidgetRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WidgetRepository widgetRepository;

    @Test
    void whenFindByArea() {
        // given
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

        // when
        Iterable<Widget> res1 = widgetRepository.findByArea(0, 0, 15, 15, null);
        Iterable<Widget> res2 = widgetRepository.findByArea(20, 20, 35, 35, null);
        Iterable<Widget> res3 = widgetRepository.findByArea(10, 10, 25, 25, null);

        // then
        assertEquals(StreamSupport.stream(res1.spliterator(), false).count(), 1);
        assertEquals(StreamSupport.stream(res2.spliterator(), false).count(), 1);
        assertEquals(StreamSupport.stream(res3.spliterator(), false).count(), 0);
    }

    @Test
    void whenGetWidgetsCountByZIndex() {
        // given
        Widget widget1 = new Widget();
        widget1.setZ(0);
        widgetRepository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setZ(1);
        widgetRepository.save((widget2));

        // when
        assertEquals(widgetRepository.getWidgetsCountByZIndex(0), 1);
        assertEquals(widgetRepository.getWidgetsCountByZIndex(1), 1);
        assertEquals(widgetRepository.getWidgetsCountByZIndex(2), 0);
    }

    @Test
    void whenGetMaxZIndex() {
        // given
        Widget widget1 = new Widget();
        widget1.setZ(0);
        widgetRepository.save(widget1);

        Widget widget2 = new Widget();
        widget1.setZ(1);
        widgetRepository.save((widget2));

        // when
        int maxIndex = widgetRepository.getMaxZIndex();

        // then
        assertEquals(maxIndex, 1);
    }

    @Test
    void whenGetMinZIndex() {
        // given
        Widget widget1 = new Widget();
        widget1.setZ(0);
        widgetRepository.save((widget1));

        Widget widget2 = new Widget();
        widget2.setZ(1);
        widgetRepository.save(widget2);

        // when
        int minIndex = widgetRepository.getMinZIndex();

        // then
        assertEquals(minIndex, 0);
    }

    @Test
    void whenIncZIndices() {
        // given
        Widget widget1 = new Widget();
        widget1.setZ(0);
        widgetRepository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setZ(1);
        widgetRepository.save(widget2);

        Widget widget3 = new Widget();
        widget3.setZ(2);
        widgetRepository.save(widget3);

        // when
        widgetRepository.incZIndices(1);
        Integer[] indices = StreamSupport.stream(widgetRepository.findAll().spliterator(), false)
                .map(x -> x.getZ())
                .sorted()
                .toArray(arr -> new Integer[arr]);

        // then
        assertArrayEquals(indices, new Integer[] { 0, 2, 3 });
    }
}