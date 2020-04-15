package com.widget.storage;

import com.widget.storage.contract.PageRequest;
import com.widget.storage.abstraction.BaseEntity;
import com.widget.storage.abstraction.IInterceptor;
import com.widget.storage.abstraction.IStorage;
import com.widget.storage.contract.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class InMemoryStorage implements IStorage {
    private ConcurrentHashMap<Type, List<BaseEntity>> entities = new ConcurrentHashMap<>();

    @Autowired(required=false)
    private IInterceptor interceptor = new EmptyInterceptor();


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T save(T obj) {
        boolean isNewEntity = false;

        if (obj.getId() == null) {
            obj.setId(obj.generateNewId());
            isNewEntity = true;
        }

        List<BaseEntity> entitiesByType = entities.getOrDefault(obj.getClass(), null);
        if (entitiesByType != null) {
            if (isNewEntity) {
                createEntity(entitiesByType, obj);
            }
            else {
                BaseEntity entity = entitiesByType.stream().filter(x -> x.getId().equals(obj.getId())).findFirst().get();
                if (entity != null) {
                    updateEntity(entitiesByType, entity, obj);
                }
                else {
                    createEntity(entitiesByType, obj);
                }
            }
        }
        else {
            List<BaseEntity> newEntityCollection = new ArrayList<>();
            entities.put(obj.getClass(), newEntityCollection);
            createEntity(newEntityCollection, obj);
        }

        return obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends  BaseEntity> List saveAll(List<T> objs) {
        for (BaseEntity obj: objs) {
            save(obj);
        }

        return objs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> Optional<T> findById(Class<T> type, UUID id) {
        return (Optional<T>) entities.getOrDefault(type, new ArrayList<>())
            .stream()
            .filter(x -> x.getId().equals(id))
            .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends  BaseEntity> List findAll(Class<T> type, Comparator<T> sort) {
        List<T> result = new ArrayList(entities.getOrDefault(type, new ArrayList<>()));

        if (sort != null) {
            result.sort(sort);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List findAll(Class<T> type, Predicate<T> filterPredicate, Comparator<T> sort) {
        return (List) findAll(type, sort).stream().filter(filterPredicate).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends  BaseEntity> PageResponse findAll(
            Class<T> type,
            @Valid PageRequest pageRequest,
            Comparator<T> sort) {
        List<T> entitiesByType = findAll(type, sort);
        if (entitiesByType == null) {
            return PageResponse.empty();
        }
        else {
            return getPagedResponse(entitiesByType, pageRequest);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> PageResponse findAll(
            Class<T> type,
            Predicate<T> filterPredicate,
            @Valid PageRequest pageRequest,
            Comparator<T> sort) {
        List<T> filteredEntitiesByType = findAll(type, filterPredicate, sort);

        return getPagedResponse(filteredEntitiesByType, pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Class type, UUID id) throws EntityNotFoundException {
        List<BaseEntity> entitiesByType = entities.getOrDefault(type, new ArrayList<>());
        BaseEntity entityToDelete = entitiesByType.stream().filter(x -> x.getId().equals(id)).findFirst().get();
        if (entityToDelete != null) {
            entitiesByType.remove(entityToDelete);
        }
        else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(BaseEntity obj) throws EntityNotFoundException {
        deleteById(obj.getClass(), (UUID)obj.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(List<BaseEntity> objs) throws EntityNotFoundException {
        for (BaseEntity obj: objs) {
            delete(obj);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(Class type) {
        entities.remove(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(Class type, UUID id) {
        return entities.getOrDefault(type, new ArrayList<>())
            .stream()
            .anyMatch(x -> x.getId().equals(id));
    }

    /**
     * Creates paged response for all records.
     * @param allRecords Records.
     * @param pageRequest Page request.
     */
    private <T extends BaseEntity> PageResponse getPagedResponse(List<T> allRecords, PageRequest pageRequest) {
        int pageStartIndex = pageRequest.getPage() * pageRequest.getPerPage(),
            pageEndIndex = Math.min((pageStartIndex + pageRequest.getPerPage()), allRecords.size());
        return pageStartIndex > pageEndIndex
            ? PageResponse.empty()
            : new PageResponse(allRecords.subList(pageStartIndex, pageEndIndex), allRecords.size());
    }

    /**
     * Creates new entity.
     * @param entitiesCollection Collection to put new entity.
     * @param newEntity Entity to put.
     */
    private void createEntity(List entitiesCollection, BaseEntity newEntity) {
        interceptor.beforeCreate(newEntity);
        entitiesCollection.add(newEntity);
        interceptor.afterCreate(newEntity);
    }

    /**
     * Updates existing entity.
     * @param entitiesCollection Collection with existing entity.
     * @param oldEntity Entity to overwrite.
     * @param newEntity Edited entity.
     */
    private void updateEntity(List<BaseEntity> entitiesCollection, BaseEntity oldEntity, BaseEntity newEntity) {
        interceptor.beforeUpdate(newEntity);
        entitiesCollection.set(entitiesCollection.indexOf(oldEntity), newEntity);
        interceptor.afterUpdate(newEntity);
    }
}
