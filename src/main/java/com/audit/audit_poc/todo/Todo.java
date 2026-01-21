package com.audit.audit_poc.todo;

import com.audit.audit_poc.entity.AbsEntity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class Todo extends AbsEntity {
    
    private String task;
    private boolean completed = false; // Default to not completed
    private String priority; // LOW, MEDIUM, HIGH
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
    
}