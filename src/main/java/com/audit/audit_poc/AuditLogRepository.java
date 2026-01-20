package com.audit.audit_poc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityId(Long entityId);
}
