package com.audit.audit_poc.audit;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audits")
public class AuditLogController {
    
    private final AuditLogRepository auditLogDao;

    public AuditLogController(AuditLogRepository auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    @GetMapping
    public List<AuditLog> getAll() {
        // Return latest logs first
        try {
			return auditLogDao.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
}
