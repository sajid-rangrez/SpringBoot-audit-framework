package com.audit.audit_poc.audit;
import java.util.Optional;
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
import org.hibernate.type.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.audit.audit_poc.AppUser;
import com.audit.audit_poc.AppUserDao;
import com.audit.audit_poc.entity.AbsEntity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpSession;

@Component
public class AuditEventListener implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener {

    private final EntityManagerFactory entityManagerFactory;
    private final AuditAsyncService auditAsyncService;
    private final AppUserDao appUserDao; // Needed to look up Company ID
    
    private final Set<String> IGNORED_FIELDS = Set.of("createdDate", "lastModifiedDate", "recordId", "password");

    public AuditEventListener(EntityManagerFactory entityManagerFactory, 
                              AuditAsyncService auditAsyncService,
                              AppUserDao appUserDao) {
        this.entityManagerFactory = entityManagerFactory;
        this.auditAsyncService = auditAsyncService;
        this.appUserDao = appUserDao;
    }

    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(this);
    }

    // ... [onPostUpdate, onPostInsert, onPostDelete methods remain the same] ...
    
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        processAudit(event.getEntity(), event.getId(), "UPDATE", event.getOldState(), event.getState(), event.getPersister());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        processAudit(event.getEntity(), event.getId(), "INSERT", null, event.getState(), event.getPersister());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        processAudit(event.getEntity(), event.getId(), "DELETE", event.getDeletedState(), null, event.getPersister());
    }

    private void processAudit(Object entity, Object id, String action, Object[] oldState, Object[] newState, EntityPersister persister) {
        if (entity instanceof AuditLog) return;
        if (!(entity instanceof AbsEntity)) return;

        // Capture Context Info ONCE per transaction/event
        String currentUsername = getUsername();
        String currentSessionId = getSessionId();
        String currentCompanyId = getCompanyId(currentUsername);

        String entityName = entity.getClass().getSimpleName();
        String entityIdStr = id.toString();
        
        String[] propertyNames = persister.getPropertyNames();
        Type[] propertyTypes = persister.getPropertyTypes();

        for (int i = 0; i < propertyNames.length; i++) {
            String fieldName = propertyNames[i];
            if (IGNORED_FIELDS.contains(fieldName) || propertyTypes[i].isCollectionType()) continue;

            Object oldVal = (oldState != null) ? oldState[i] : null;
            Object newVal = (newState != null) ? newState[i] : null;

            if (isChanged(oldVal, newVal) || "DELETE".equals(action)) {
                saveAudit(entityName, entityIdStr, action, fieldName, oldVal, newVal, 
                          currentUsername, currentSessionId, currentCompanyId);
            }
        }
    }

    private void saveAudit(String entity, String id, String action, String field, Object oldVal, Object newVal,
                           String user, String session, String company) {
        AuditLog log = new AuditLog();
        log.setEntityName(entity);
        log.setEntityId(id);
        log.setAction(action);
        log.setFieldName(field);
        log.setOldValue(truncate(formatValue(oldVal), 1000));
        log.setNewValue(truncate(formatValue(newVal), 1000));
        
        // Set Captured Context
        log.setModifiedBy(user);
        log.setSessionId(session);
        log.setCompanyId(company);

        auditAsyncService.saveAuditLog(log);
    }

    // --- CONTEXT HELPERS ---

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "System/Unknown";
    }

    private String getSessionId() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attr != null) {
                HttpSession session = attr.getRequest().getSession(false); // Don't create if not exists
                if (session != null) return session.getId();
            }
        } catch (Exception e) {
            // Context might not be available in async threads
        }
        return "No-Session";
    }

    private String getCompanyId(String username) {
        if ("System/Unknown".equals(username)) return "N/A";
        // In a real app, cache this lookup or store it in the Principal to avoid DB hit
        Optional<AppUser> user = appUserDao.findByUsername(username);
        return user.map(AppUser::getCompanyId).orElse("Unknown");
    }

    // ... [truncate, formatValue, isChanged, requiresPostCommitHandling helpers remain the same] ...
    
    private String truncate(String value, int length) {
        if (value != null && value.length() > length) return value.substring(0, length - 3) + "...";
        return value;
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof java.util.Collection) return "[Ignored Collection]";
        if (value instanceof AbsEntity) return "Entity: " + value.getClass().getSimpleName() + " [ID: " + ((AbsEntity) value).getRecordId() + "]";
        return value.toString();
    }

    private boolean isChanged(Object oldVal, Object newVal) {
        return (oldVal == null && newVal != null) || (oldVal != null && !oldVal.equals(newVal));
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) { return false; }
}