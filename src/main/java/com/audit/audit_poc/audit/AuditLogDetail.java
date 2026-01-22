package com.audit.audit_poc.audit;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AuditLogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Link back to the main log
    @JsonIgnoreProperties("details")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_log_id")
    private AuditLog auditLog;

    private String columnName;

    @Column(length = 1000) // Allow longer text for values
    private String oldValue;

    @Column(length = 1000)
    private String newValue;

    // Constructors, Getters, Setters
    public AuditLogDetail() {}

    public AuditLogDetail(String columnName, String oldValue, String newValue) {
        this.columnName = columnName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public AuditLog getAuditLog() { return auditLog; }
    public void setAuditLog(AuditLog auditLog) { this.auditLog = auditLog; }
    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
}