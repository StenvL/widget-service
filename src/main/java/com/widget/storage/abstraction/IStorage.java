package com.widget.storage.abstraction;

import com.widget.storage.contract.PageRequest;
import com.widget.storage.EntityNotFoundException;
import com.widget.storage.contract.PageResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface IStorage {
    /**
     * Saves entity into storage.
     * @param obj Entity to save.
     */
    <T extends BaseEntity> T save(T obj);

    /**
     * Saves collection of entities in storage.
     * @param objs Entities collection.
     */
    <T extends BaseEntity> List saveAll(List<T> objs);

    /**
     * Returns entity by its type and identifier.
     * @param type Entity type.
     * @param id Entity identifier.
     */
    <T extends BaseEntity> Optional<T> findById(Class<T> type, UUID id);

    /**
     * Returns all entities by type.
     * @param type Entity type.
     * @param sort Comparator to sort.
     */
    <T extends BaseEntity> List findAll(Class<T> type, Comparator<T> sort);

    /**
     * Returns entities filtered by predicate.
     * @param type Entity type.
     * @param filterPredicate Predicate to filter.
     * @param sort Comparator to sort.
     */
    <T extends BaseEntity> List findAll(Class<T> type, Predicate<T> filterPredicate, Comparator<T> sort);

    /**
     * Returns page of entities by type.
     * @param type Entity type.
     * @param pageRequest Page request.
     * @param sort Comparator to sort.
     */
    <T extends BaseEntity> PageResponse findAll(Class<T> type, PageRequest pageRequest, Comparator<T> sort);

    /**
     * Returns page of entities by type.
     * @param type Entity type.
     * @param filterPredicate Predicate to filter.
     * @param pageRequest Page request.
     * @param sort Comparator to sort.
     */
    <T extends BaseEntity> PageResponse findAll(
        Class<T> type,
        Predicate<T> filterPredicate,
        PageRequest pageRequest,
        Comparator<T> sort);

    /**
     * Deletes entity by its type and identifier.
     * @param type Entity type.
     * @param id Entity identifier.
     * @throws EntityNotFoundException
     */
    void deleteById(Class type, UUID id) throws EntityNotFoundException;

    /**
     * Deletes entity.
     * @param obj Entity to delete.
     */
    void delete(BaseEntity obj) throws EntityNotFoundException;

    /**
     * Deletes entities.
     * @param objs Entities to delete.
     */
    void deleteAll(List<BaseEntity> objs) throws EntityNotFoundException;

    /**
     * Deletes all entities by type.
     * @param type Type.
     */
    void deleteAll(Class type);

    /**
     * Check whether entity exists in storage.
     * @param type Entity type.
     * @param id Entity identifier.
     */
    boolean exists(Class type, UUID id);
}
