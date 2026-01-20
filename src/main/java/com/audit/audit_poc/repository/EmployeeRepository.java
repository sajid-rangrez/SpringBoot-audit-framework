package com.audit.audit_poc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.audit.audit_poc.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {}