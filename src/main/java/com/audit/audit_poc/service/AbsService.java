package com.audit.audit_poc.service;

import java.util.List;
import java.util.UUID;

import com.audit.audit_poc.entity.AbsEntity;

public interface AbsService<T extends AbsEntity> {
    T save(T entity);
    T findById(UUID id);
    List<T> findAll();
    void deleteById(UUID id);
}