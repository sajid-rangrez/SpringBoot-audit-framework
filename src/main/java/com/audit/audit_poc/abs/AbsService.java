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
        // 1. If it's a New Creation (ID is null), just save it
        if (entity.getRecordId() == null) {
            return repository.save(entity);
        }

        // 2. If it's an Update (ID exists), fetch the DB version first
        return repository.findById(entity.getRecordId())
                .map(existing -> {
                    // 3. Copy ONLY the changed fields (Name, Title, etc.)
                    // This ignores the 'employees' list, so relationships stay safe!
                    UpdateUtils.copyNonNullProperties(entity, existing);
                    
                    // 4. Save the EXISTING object (which still has the employees attached)
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    // Fallback: If ID was provided but not found in DB, treat as new
                    return repository.save(entity);
                });
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}