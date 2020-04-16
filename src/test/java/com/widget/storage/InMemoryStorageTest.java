package com.widget.storage;

import com.widget.service.WidgetServiceApp;
import com.widget.service.model.Widget;
import com.widget.storage.abstraction.IStorage;
import com.widget.storage.contract.PageRequest;
import com.widget.storage.contract.PageResponse;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WidgetServiceApp.class)
class InMemoryStorageTest {
    @Autowired
    private IStorage storage;

    @BeforeEach
    public void init() {
        storage.deleteAll(Widget.class);
    }

    @Test
    void save_CreateFirstRecordOfType_SuccessfullySaved() {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);
        UUID id = widgetToSave.getId();

        // when
        Optional<Widget> widget = storage.findById(Widget.class, id);

        // then
        assertTrue(widget.isPresent() && widget.get().getId().equals(id));
    }

    @Test
    void save_CreateNonFirstRecordOfType_SuccessfullySaved() {
        // given
        Widget widget1 = getWidget();
        storage.save(widget1);

        Widget widget2 = getWidget();
        storage.save(widget2);
        UUID id = widget2.getId();

        // when
        Optional<Widget> widget = storage.findById(Widget.class, id);

        // then
        assertTrue(widget.isPresent() && widget.get().getId().equals(id));
    }

    @Test
    void save_NewEntityWithoutId_SuccessfullySaved() {
        // given
        Widget widget1 = getWidget();
        storage.save(widget1);

        Widget widgetToSave = getWidget();

        // when
        storage.save(widgetToSave);
        UUID id = widgetToSave.getId();

        // then
        assertEquals(storage.findById(Widget.class, id).get().getId(), id);
    }

    @Test
    void save_NewEntityWithId_SuccessfullySaved() {
        // given
        Widget widget1 = getWidget();
        storage.save(widget1);

        Widget widgetToSave = getWidget();
        UUID id = widgetToSave.generateNewId();
        widgetToSave.setId(id);

        // when
        storage.save(widgetToSave);

        // then
        assertEquals(storage.findById(Widget.class, id).get().getId(), id);
    }

    @Test
    void save_ExistingEntity_ItUpdates() {
        // given
        int zIndexBeforeUpdate = 1;

        Widget widgetToSave = getWidget();
        widgetToSave.setZ(zIndexBeforeUpdate);
        storage.save(widgetToSave);
        UUID id = widgetToSave.getId();

        assertEquals(storage.findById(Widget.class, id).get().getZ(), zIndexBeforeUpdate);

        // when
        int zIndexAfterUpdate = 2;

        Widget widgetToUpdate = getWidget();
        widgetToUpdate.setId(id);
        widgetToUpdate.setZ(zIndexAfterUpdate);
        storage.save(widgetToUpdate);

        // then
        assertEquals(storage.findById(Widget.class, id).get().getZ(), zIndexAfterUpdate);
    }

    @Test
    void saveAll_CollectionOfEntities_SuccessfullySaved() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();

        List<Widget> widgets = new ArrayList<>(List.of(widget1, widget2));

        // when
        storage.saveAll(widgets);

        // then
        assertEquals(storage.findAll(Widget.class, null).size(), 2);
    }

    @Test
    void findById_ExistingEntityId_ReturnsEntity() {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);

        UUID id = widgetToSave.getId();

        // when
        Widget loadedWidget = storage.findById(Widget.class, id).get();

        // then
        assertEquals(loadedWidget.getId(), id);
    }

    @Test
    void findById_NonExistingEntityId_NotPresent() {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);

        // when
        Optional<Widget> loadedWidget = storage.findById(Widget.class, UUID.randomUUID());

        // then
        assertFalse(loadedWidget.isPresent());
    }

    @Test
    void findAll_WithoutSorting_ReturnsAllRecords() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();
        Widget widget3 = getWidget();
        widget1.setZ(3);
        widget1.setZ(1);
        widget1.setZ(2);

        storage.saveAll(new ArrayList<>(List.of(widget1, widget2, widget3)));

        // when
        List widgets = storage.findAll(Widget.class, null);

        // then
        assertEquals(widgets.size(), 3);
    }

    @Test
    void findAll_WithSorting_ReturnsAllRecordsSorted() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();
        Widget widget3 = getWidget();
        widget1.setZ(3);
        widget2.setZ(1);
        widget3.setZ(2);

        storage.saveAll(new ArrayList<>(List.of(widget1, widget2, widget3)));

        // when
        List<Widget> widgets = storage.findAll(Widget.class, Comparator.comparing(x -> x.getZ()));

        // then
        int[] expected = { 1, 2, 3 };
        assertArrayEquals(widgets.stream().mapToInt(Widget::getZ).toArray(), expected);
    }

    @Test
    void findAll_WithSortingAndFiltering_ReturnsFilteredAndSortedRecords() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();
        Widget widget3 = getWidget();
        widget1.setZ(3);
        widget2.setZ(2);
        widget3.setZ(1);

        storage.saveAll(new ArrayList<>(List.of(widget1, widget2, widget3)));

        // when
        List<Widget> widgets = storage.findAll(Widget.class, x -> x.getZ() > 1, Comparator.comparing(x -> x.getZ()));

        // then
        int[] expected = { 2, 3 };
        assertArrayEquals(widgets.stream().mapToInt(Widget::getZ).toArray(), expected);
    }

    @Test
    void findAll_WithPagingAndSorting_ReturnsPageOfSortedRecords() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();
        Widget widget3 = getWidget();
        widget1.setZ(3);
        widget2.setZ(2);
        widget3.setZ(1);

        storage.saveAll(new ArrayList<>(List.of(widget1, widget2, widget3)));

        // when
        PageResponse<Widget> widgets = storage.findAll(
                Widget.class,
                new PageRequest(1, 2),
                Comparator.comparing(x -> x.getZ()));

        // then
        int[] expected = { 3 };
        assertArrayEquals(widgets.getRecords().stream().mapToInt(Widget::getZ).toArray(), expected);
    }

    @Test
    void findAll_WithPagingAndSortingEmptyCollection_ReturnsEmptyPageOfRecords() {
        // when
        PageResponse<Widget> widgets = storage.findAll(
                Widget.class,
                new PageRequest(1, 2),
                Comparator.comparing(x -> x.getZ()));

        // then
        int[] expected = { };
        assertArrayEquals(widgets.getRecords().stream().mapToInt(Widget::getZ).toArray(), expected);
    }

    @Test
    void findAll_WithPagingAndSortingAndFiltering_ReturnsPageOfSortedAndFilteredRecords() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();
        Widget widget3 = getWidget();
        Widget widget4 = getWidget();
        widget1.setZ(1);
        widget2.setZ(1);
        widget3.setZ(2);
        widget4.setZ(3);

        storage.saveAll(new ArrayList<>(List.of(widget1, widget2, widget3, widget4)));

        // when
        PageResponse<Widget> widgets = storage.findAll(
                Widget.class,
                x -> x.getZ() < 3,
                new PageRequest(1, 2),
                Comparator.comparing(x -> x.getZ()));

        // then
        int[] expected = { 2 };
        assertArrayEquals(widgets.getRecords().stream().mapToInt(Widget::getZ).toArray(), expected);
    }

    @Test
    void deleteById_ExistingEntityId_SuccessfullyDeleted() throws EntityNotFoundException {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);
        UUID id = widgetToSave.getId();

        Optional<Widget> loadedWidget = storage.findById(Widget.class, id);
        assertTrue(loadedWidget.isPresent() && loadedWidget.get().getId().equals(id));

        // when
        storage.deleteById(Widget.class, id);
        loadedWidget = storage.findById(Widget.class, id);

        // then
        assertFalse(loadedWidget.isPresent());
    }

    @Test
    void deleteById_NonExistingEntityId_ThrowsEntityNotFoundException() {
        UUID id  = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> storage.deleteById(Widget.class, id));
    }

    @Test
    void delete_ExistingEntity_SuccessfullyDeleted() throws EntityNotFoundException {
        // given
        Widget widgetToSave = getWidget();
        storage.save(widgetToSave);
        UUID id = widgetToSave.getId();

        Optional<Widget> loadedWidget = storage.findById(Widget.class, id);
        assertTrue(loadedWidget.isPresent() && loadedWidget.get().getId().equals(id));

        // when
        storage.delete(widgetToSave);
        loadedWidget = storage.findById(Widget.class, id);

        // then
        assertFalse(loadedWidget.isPresent());
    }

    @Test
    void delete_NonExistingEntity_ThrowsEntityNotFoundException() {
        Widget widgetToDelete = new Widget();
        widgetToDelete.setId(widgetToDelete.generateNewId());
        assertThrows(EntityNotFoundException.class, () -> storage.delete(widgetToDelete));
    }

    @Test
    void deleteAll_ExistingCollection_DeletedSuccessfully() {
        // given
        Widget widget1 = getWidget();
        Widget widget2 = getWidget();

        storage.save(widget1);
        storage.save(widget2);

        assertEquals(storage.findAll(Widget.class, null).size(), 2);

        // when
        storage.deleteAll(Widget.class);

        // then
        assertEquals(storage.findAll(Widget.class, null).size(), 0);
    }

    @Test
    void exists_ExistedEntity_ReturnsTrue() {
        Widget widget = getWidget();
        storage.save(widget);
        UUID id = widget.getId();

        assertTrue(storage.exists(Widget.class, id));
    }

    @Test
    void exists_NonExistedEntity_ReturnsFalse() {
        assertFalse(storage.exists(Widget.class, UUID.randomUUID()));
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