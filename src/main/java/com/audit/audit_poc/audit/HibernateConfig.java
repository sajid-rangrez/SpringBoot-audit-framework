package com.audit.audit_poc.audit;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class HibernateConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private AuditHibernateListener auditListener;

    @PostConstruct
    public void registerListeners() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        // Register the listener for Insert, Update, and Delete
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(auditListener);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(auditListener);
        registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(auditListener);
    }
}