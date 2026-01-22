package com.audit.audit_poc.employee;

import com.audit.audit_poc.abs.AbsEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = true)
public class Employee extends AbsEntity{

	private String name;
    private String role;

    // Many Employees belong to One Department
    @JsonIgnoreProperties("employees")
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
