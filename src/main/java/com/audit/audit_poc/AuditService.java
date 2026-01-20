package com.audit.audit_poc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // REQUIRES_NEW creates a "side transaction" just for the log
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(AuditLog log) {
        auditLogRepository.save(log);
    }
}