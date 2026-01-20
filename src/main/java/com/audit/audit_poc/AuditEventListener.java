package com.audit.audit_poc;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

@Component
public class AuditEventListener implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener { // <--- Added PostDeleteEventListener

    private final EntityManagerFactory entityManagerFactory;
    private final AuditService auditService;
    
    // Fields to ignore during audit
    private final Set<String> IGNORED_FIELDS = Set.of("createdDate", "lastModifiedDate");

    public AuditEventListener(EntityManagerFactory entityManagerFactory, AuditService auditService) {
        this.entityManagerFactory = entityManagerFactory;
        this.auditService = auditService;
    }

    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(this); // <--- Register Delete Listener
    }

    // --- INSERT (CREATE) ---
    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof AuditLog) return;

        Long entityId = castToLong(event.getId());
        String entityName = event.getEntity().getClass().getSimpleName();
        Object[] newState = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();

        for (int i = 0; i < propertyNames.length; i++) {
            String fieldName = propertyNames[i];
            if (IGNORED_FIELDS.contains(fieldName)) continue;
            
            saveAudit(entityName, entityId, "INSERT", fieldName, null, newState[i]);
        }
    }

    // --- UPDATE ---
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof AuditLog) return;

        Long entityId = castToLong(event.getId());
        String entityName = event.getEntity().getClass().getSimpleName();
        Object[] oldState = event.getOldState();
        Object[] newState = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();

        for (int i = 0; i < propertyNames.length; i++) {
            String fieldName = propertyNames[i];
            if (IGNORED_FIELDS.contains(fieldName)) continue;

            Object oldVal = oldState[i];
            Object newVal = newState[i];

            if (isChanged(oldVal, newVal)) {
                saveAudit(entityName, entityId, "UPDATE", fieldName, oldVal, newVal);
            }
        }
    }

    // --- DELETE ---
    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof AuditLog) return;

        Long entityId = castToLong(event.getId());
        String entityName = event.getEntity().getClass().getSimpleName();
        Object[] oldState = event.getDeletedState(); // <--- Deleted entities have "Old State" but no "New State"
        String[] propertyNames = event.getPersister().getPropertyNames();

        for (int i = 0; i < propertyNames.length; i++) {
            String fieldName = propertyNames[i];
            if (IGNORED_FIELDS.contains(fieldName)) continue;

            Object oldVal = oldState[i];
            
            // For DELETE: Old Value is what was deleted, New Value is "null" or "DELETED"
            saveAudit(entityName, entityId, "DELETE", fieldName, oldVal, null);
        }
    }

 // Inside AuditEventListener.java

    // ... (Your init, onPostInsert, onPostUpdate, onPostDelete methods remain the same) ...

    private void saveAudit(String entity, Long id, String action, String field, Object oldVal, Object newVal) {
        AuditLog log = new AuditLog();
        log.setEntityName(entity);
        log.setEntityId(id);
        log.setAction(action);
        log.setFieldName(field);
        
        // --- RELATIONSHIP FIX ---
        // Convert objects to clean Strings (handles Entities by extracting ID)
        log.setOldValue(formatValue(oldVal));
        log.setNewValue(formatValue(newVal));
        // ------------------------

        log.setTimestamp(LocalDateTime.now());
        log.setModifiedBy("System"); 

        auditService.saveLog(log);
    }

    // New Helper to extract IDs from related entities
    private String formatValue(Object value) {
        if (value == null) return "null";
        
        // Check if the value is one of our Entities (Department or Employee)
        // You can check by class or by annotation
        if (value.getClass().isAnnotationPresent(jakarta.persistence.Entity.class)) {
            try {
                // Use reflection or casting to get ID. 
                // Since we know our entities have getId(), we can cast if we are sure, 
                // or use a safer reflection approach:
                var method = value.getClass().getMethod("getId");
                Object id = method.invoke(value);
                return  id.toString() ;
            } catch (Exception e) {
                return value.toString(); // Fallback
            }
        }
        
        return value.toString();
    }

    private Long castToLong(Object id) {
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null; 
    }

    private boolean isChanged(Object oldVal, Object newVal) {
        return (oldVal == null && newVal != null) || (oldVal != null && !oldVal.equals(newVal));
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}