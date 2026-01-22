package com.audit.audit_poc.abs;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

// T is the Entity, UUID is the ID type
@NoRepositoryBean
public interface AbsDao<T extends AbsEntity> extends JpaRepository<T, UUID> {
}