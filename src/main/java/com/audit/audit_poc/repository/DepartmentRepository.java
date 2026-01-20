package com.audit.audit_poc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.audit.audit_poc.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {}