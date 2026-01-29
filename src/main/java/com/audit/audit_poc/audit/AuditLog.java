package com.audit.audit_poc.audit;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "record_id", updatable = false, nullable = false)
    private UUID recordId;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	private String userID;

	@Column(name = "session_id")
	private String sessionId;

	@Column(name = "reason")
	private String reason;

	@Column(name = "contextType")
	private String contextType;

	@Column(name = "contextId")
	private String contextId;
	
	@Column(name = "comp_unit")
	private String companyUnit;
	
	@Column(name = "action")
	private String action;

	public String getCompanyUnit() {
		return companyUnit;
	}

	public void setCompanyUnit(String companyUnit) {
		this.companyUnit = companyUnit;
	}

	@JsonIgnoreProperties("auditLog")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "auditLog", targetEntity = AuditFields.class)
	private Collection<AuditFields> auditFields = new LinkedHashSet<AuditFields>();

	@CreatedDate
	@Column(name = "timestamp", updatable = false)
	private LocalDateTime timestamp;

	@Transient
	private Collection<AuditFields> tempAuditFields = new LinkedHashSet<AuditFields>();

    public void addDetail(String tableName, String column, String oldVal, String newVal, String sessionID) {
    	AuditFields fields = new AuditFields(this, oldVal, newVal, column,tableName,sessionID);
//        fields.setAuditLog(this);
        this.auditFields.add(fields);
    }
    
	public AuditLog() {
	}

	public AuditLog(String userID, String sessionId, String reason, String contextType, String contextId,
			Collection<AuditFields> auditFields, LocalDateTime timestamp) {
		this.userID = userID;
		this.sessionId = sessionId;
		this.reason = reason;
		this.contextType = contextType;
		this.contextId = contextId;
		this.auditFields = auditFields;
		this.timestamp = timestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public Collection<AuditFields> getAuditFields() {
		return auditFields;
	}

	public void setAuditFields(Collection<AuditFields> auditFields) {
		this.auditFields = auditFields;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((contextId == null) ? 0 : contextId.hashCode());
		result = prime * result + ((contextType == null) ? 0 : contextType.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuditLog other = (AuditLog) obj;
		if (contextId == null) {
			if (other.contextId != null)
				return false;
		} else if (!contextId.equals(other.contextId))
			return false;
		if (contextType == null) {
			if (other.contextType != null)
				return false;
		} else if (!contextType.equals(other.contextType))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

	public Collection<AuditFields> getTempAuditFields() {
		return tempAuditFields;
	}

	public void setTempAuditFields(Collection<AuditFields> tempAuditFields) {
		this.tempAuditFields = tempAuditFields;
	}

	public UUID getRecordId() {
		return recordId;
	}

	public void setRecordId(UUID recordId) {
		this.recordId = recordId;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}