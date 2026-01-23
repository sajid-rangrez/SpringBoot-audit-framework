package com.audit.audit_poc.audit;


import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.AbstractEntityPersister;
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

    // ... (onPreInsert, onPreUpdate, onPreDelete methods remain exactly the same) ...
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
        if (!(entity instanceof AbsEntity)) return;
        if (entity instanceof AuditLog || entity instanceof AuditLogDetail) return;

        AbsEntity absEntity = (AbsEntity) entity;
        
        // --- CHANGE 1: Get Real DB Table Name ---
        String tableName;
        if (persister instanceof AbstractEntityPersister) {
            // This returns the actual table name (e.g., "todo_tbl" or "T_TODO")
            tableName = ((AbstractEntityPersister) persister).getTableName();
        } else {
            // Fallback if something weird happens
            tableName = entity.getClass().getSimpleName();
        }

        AuditLog log = new AuditLog();
        log.setEntityName(tableName); // Storing Table Name instead of Class Name
        log.setEntityId(absEntity.getRecordId() != null ? absEntity.getRecordId().toString() : "PENDING");
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());

        String[] propertyNames = persister.getPropertyNames();
        AbstractEntityPersister abstractPersister = (persister instanceof AbstractEntityPersister) 
                                                    ? (AbstractEntityPersister) persister 
                                                    : null;

        for (int i = 0; i < propertyNames.length; i++) {
            
        	// --- FIX: SKIP TIMESTAMP FIELDS ---
        	String propName = propertyNames[i];
            if ("createdDate".equals(propName) || "lastModifiedDate".equals(propName)) {
                continue; 
            }
            
            // --- CHANGE 2: Get Real DB Column Name ---
            String dbColumnName = propertyNames[i]; // Default to field name
            
            if (abstractPersister != null) {
                // Hibernate allows a field to map to multiple columns, but usually it's just 1.
                // We take the first one.
                String[] cols = abstractPersister.getPropertyColumnNames(propertyNames[i]);
                if (cols != null && cols.length > 0) {
                    // Remove quotes if Hibernate adds them (e.g., `task_name` -> task_name)
                    dbColumnName = cols[0].replaceAll("`", "").replaceAll("\"", "");
                }
            }

            Object oldVal = (oldState != null) ? oldState[i] : null;
            Object newVal = (newState != null) ? newState[i] : null;

            if ("UPDATE".equals(action)) {
                if (isChanged(oldVal, newVal)) {
                    log.addDetail(dbColumnName, format(oldVal), format(newVal));
                }
            } else if ("INSERT".equals(action)) {
                if (newVal != null) {
                    log.addDetail(dbColumnName, "null", format(newVal));
                }
            } else if ("DELETE".equals(action)) {
                if (oldVal != null) {
                    log.addDetail(dbColumnName, format(oldVal), "null");
                }
            }
        }

        if (!log.getDetails().isEmpty() || !"UPDATE".equals(action)) {
            auditService.saveAuditLog(log);
        }
    }

    private boolean isChanged(Object oldVal, Object newVal) {
        if (oldVal == null && newVal == null) return false;
        if (oldVal == null || newVal == null) return true;
        if (oldVal instanceof LocalDateTime && newVal instanceof LocalDateTime) return false;
        return !oldVal.equals(newVal);
    }

//    private String format(Object val) {
//        return val != null ? val.toString() : "null";
//    }
    private String format(Object val) {
        if (val == null) return "null";

        // Check if the value is one of YOUR Entities (e.g., Department object inside Employee)
        if (val instanceof AbsEntity) {
            try {
                // 1. Try to find a 'getName()' method to return the readable name
                // This satisfies your requirement: "old value IT, new value HR"
                Method getName = val.getClass().getMethod("getName");
                Object name = getName.invoke(val);
                return name != null ? name.toString() : "";
            } catch (Exception e) {
                // 2. If entity has no 'name' field, fallback to just the ID
                return ((AbsEntity) val).getRecordId().toString();
            }
        }

        // Default behavior for simple strings, dates, numbers
        return val.toString();
    }
}