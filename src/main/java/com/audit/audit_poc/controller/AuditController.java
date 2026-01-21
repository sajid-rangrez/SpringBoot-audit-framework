package com.audit.audit_poc.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.audit.AuditLog;
import com.audit.audit_poc.audit.AuditLogDao;

@RestController
@RequestMapping("/api/audits")
public class AuditController {
    private final AuditLogDao dao;

    public AuditController(AuditLogDao dao) {
        this.dao = dao;
    }

    @GetMapping
    public List<AuditLog> getLogs() {
        return dao.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
    }
}