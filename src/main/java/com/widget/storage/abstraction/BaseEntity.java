package com.widget.storage.abstraction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEntity<T> {
    private T id;

    /**
     * Generates new identifier for entity.
     */
    public T generateNewId() { return null; };
}
