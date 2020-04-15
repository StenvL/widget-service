package com.widget.storage.abstraction;

/**
 * Describes interceptor.
 */
public interface IInterceptor {
    /**
     * Executes before instance created.
     * @param entity Entity.
     */
    void beforeCreate(BaseEntity entity);

    /**
     * Executes after instance created.
     * @param entity Entity.
     */
    void afterCreate(BaseEntity entity);

    /**
     * Executes before instance updated.
     * @param entity Entity.
     */
    void beforeUpdate(BaseEntity entity);

    /**
     * Executes after instance updated.
     * @param entity Entity.
     */
    void afterUpdate(BaseEntity entity);

    /**
     * Executes after instance deleted.
     * @param entity Entity.
     */
    void beforeDelete(BaseEntity entity);

    /**
     * Executes after instance deleted.
     * @param entity Entity.
     */
    void afterDelete(BaseEntity entity);
}
