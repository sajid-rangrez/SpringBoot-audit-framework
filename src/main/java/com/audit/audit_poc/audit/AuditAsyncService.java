package com.audit.audit_poc.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditAsyncService {

    private final AuditLogDao auditLogDao;

    public AuditAsyncService(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    // This MUST be in a separate class for REQUIRES_NEW to work properly
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog log) {
        try {
            auditLogDao.save(log);
        } catch (Exception e) {
            // Swallow audit errors so they don't roll back the main business transaction
            System.err.println("Failed to save audit log: " + e.getMessage());
        }
    }

	public AuditLogDao getAuditLogDao() {
		return auditLogDao;
	}
    
}