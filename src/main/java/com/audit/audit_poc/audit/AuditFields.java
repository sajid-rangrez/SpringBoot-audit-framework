package com.audit.audit_poc.audit;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "record_id", updatable = false, nullable = false)
    private UUID recordId;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_recordId")
	private AuditLog auditLog;

	@Column(name = "old_column_value")
	private String oldValue;

	@Column(name = "new_column_value")
	private String newValue;

	@Column(name = "column_name")
	private String columnName;

	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "session_id")
	private String sessionId;

	public AuditFields() {
	}



	public AuditFields(AuditLog auditLog, String oldValue, String newValue, String columnName, String tableName,
			String sessionId) {
		this.auditLog = auditLog;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.columnName = columnName;
		this.tableName = tableName;
		this.sessionId = sessionId;
	}



	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}



	public String getSessionId() {
		return sessionId;
	}



	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	
}