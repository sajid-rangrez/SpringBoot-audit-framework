package com.audit.audit_poc.audit;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String entityName;
    private String action; 
    private String entityId;
    private LocalDateTime timestamp;

    // One Log has many Changes
    @JsonIgnoreProperties("auditLog")
    @OneToMany(mappedBy = "auditLog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AuditLogDetail> details = new ArrayList<>();

    // Helper to add details easily
    public void addDetail(String column, String oldVal, String newVal) {
        AuditLogDetail detail = new AuditLogDetail(column, oldVal, newVal);
        detail.setAuditLog(this);
        this.details.add(detail);
    }

    // Getters and Setters (Manual)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public List<AuditLogDetail> getDetails() { return details; }
    public void setDetails(List<AuditLogDetail> details) { this.details = details; }
}