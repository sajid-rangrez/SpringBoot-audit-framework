package com.audit.audit_poc.abs;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.ToString;

@MappedSuperclass
@ToString
@EntityListeners(AuditingEntityListener.class)
//@EntityListeners({AuditingEntityListener.class, AuditListener.class})
public abstract class AbsEntity {

    @Id
    @UuidGenerator
    @Column(name = "record_id", updatable = false, nullable = false)
    private UUID recordId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

	public UUID getRecordId() {
		return recordId;
	}

	public void setRecordId(UUID recordId) {
		this.recordId = recordId;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
    
}