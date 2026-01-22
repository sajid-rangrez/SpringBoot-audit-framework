package com.audit.audit_poc.audit;


import java.time.LocalDateTime;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.audit.audit_poc.abs.AbsEntity;

@Component
public class AuditHibernateListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    @Lazy
    @Autowired
    private AuditService auditService;

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        audit(event.getEntity(), event.getPersister(), event.getState(), null, "INSERT");
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        audit(event.getEntity(), event.getPersister(), event.getState(), event.getOldState(), "UPDATE");
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        audit(event.getEntity(), event.getPersister(), null, event.getDeletedState(), "DELETE");
        return false;
    }

    private void audit(Object entity, EntityPersister persister, Object[] newState, Object[] oldState, String action) {
        if (!(entity instanceof AbsEntity)) {
            return; // Only audit our AbstractEntities
        }

        AbsEntity absEntity = (AbsEntity) entity;
        
        // Skip auditing the AuditLogs themselves to avoid infinite loops
        if (entity instanceof AuditLog || entity instanceof AuditLogDetail) {
            return;
        }

        AuditLog log = new AuditLog();
        log.setEntityName(entity.getClass().getSimpleName());
        log.setEntityId(absEntity.getRecordId() != null ? absEntity.getRecordId().toString() : "PENDING");
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());

        String[] propertyNames = persister.getPropertyNames();

        // 1. UPDATE: Compare Old vs New
        if ("UPDATE".equals(action)) {
            for (int i = 0; i < propertyNames.length; i++) {
                Object oldVal = oldState[i];
                Object newVal = newState[i];

                // Check if value changed
                if (isChanged(oldVal, newVal)) {
                    log.addDetail(propertyNames[i], format(oldVal), format(newVal));
                }
            }
        } 
        // 2. INSERT: Everything is New
        else if ("INSERT".equals(action)) {
            for (int i = 0; i < propertyNames.length; i++) {
                if (newState[i] != null) {
                    log.addDetail(propertyNames[i], "null", format(newState[i]));
                }
            }
        } 
        // 3. DELETE: Everything is Old
        else if ("DELETE".equals(action)) {
            for (int i = 0; i < propertyNames.length; i++) {
                if (oldState[i] != null) {
                    log.addDetail(propertyNames[i], format(oldState[i]), "null");
                }
            }
        }

        // Only save if there are details or it's a delete/insert (updates with no changes shouldn't log)
        if (!log.getDetails().isEmpty() || !"UPDATE".equals(action)) {
            auditService.saveAuditLog(log);
        }
    }

    private boolean isChanged(Object oldVal, Object newVal) {
        if (oldVal == null && newVal == null) return false;
        if (oldVal == null || newVal == null) return true;
        
        // Ignore LastUpdatedDate changes if that's the ONLY change
        if (oldVal instanceof LocalDateTime && newVal instanceof LocalDateTime) return false;
        
        return !oldVal.equals(newVal);
    }

    private String format(Object val) {
        return val != null ? val.toString() : "null";
    }
}