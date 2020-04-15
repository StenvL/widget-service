package com.widget.service.model;

import com.widget.storage.abstraction.BaseEntity;

import java.util.UUID;

public class Entity extends BaseEntity<UUID> {
    @Override
    public UUID generateNewId() {
        return UUID.randomUUID();
    }
}
