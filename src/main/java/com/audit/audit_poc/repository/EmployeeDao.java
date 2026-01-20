package com.audit.audit_poc.repository;

import org.springframework.stereotype.Repository;

import com.audit.audit_poc.entity.Employee;

@Repository
public interface EmployeeDao extends AbsDao<Employee> {}