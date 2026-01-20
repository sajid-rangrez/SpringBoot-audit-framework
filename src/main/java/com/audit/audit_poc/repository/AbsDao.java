package com.audit.audit_poc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.audit.audit_poc.entity.AbsEntity;

// T is the Entity, UUID is the ID type
@NoRepositoryBean
public interface AbsDao<T extends AbsEntity> extends JpaRepository<T, UUID> {
}