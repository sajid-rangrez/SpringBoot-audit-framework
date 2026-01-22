package com.audit.audit_poc.abs;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

public abstract class AbsService<T extends AbsEntity, R extends AbsDao<T>> {

    protected final R repository;
    
    public AbsService(R repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}