package com.audit.audit_poc.audit;

import org.springframework.stereotype.Repository;

import com.audit.audit_poc.repository.AbsDao;

@Repository
public interface AuditLogDao extends AbsDao<AuditLog> {}
