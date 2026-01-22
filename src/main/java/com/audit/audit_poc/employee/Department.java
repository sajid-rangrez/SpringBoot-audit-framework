package com.audit.audit_poc.employee;

import java.util.ArrayList;
import java.util.List;

import com.audit.audit_poc.abs.AbsEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = true)
public class Department extends AbsEntity{

	private String name;

	@JsonIgnoreProperties("department")
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Prevent infinite loops in logs
    private List<Employee> employees = new ArrayList<>();
    
 // --- Helper Methods for Relationship Management ---
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }
}
