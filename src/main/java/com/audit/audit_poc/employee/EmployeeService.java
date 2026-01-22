package com.audit.audit_poc.employee;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.audit.audit_poc.abs.AbsService;

@Service
public class EmployeeService extends AbsService<Employee, EmployeeDao> {
	public EmployeeService(EmployeeDao repository) {
		super(repository);
	}
	
	@Autowired
	private DepartmentDao departmentDao;
//	
//	public Employee save(Employee employee) {
//        return repository.saveAndFlush(employee);
//    }
//	
//	public Employee update(UUID id, Employee incoming) {
//        Employee existing = repository.findById(id).orElseThrow();
//        
//        // 1. Update basic fields
//        existing.setName(incoming.getName());
//        existing.setRole(incoming.getRole());
//
//        // 2. Handle Department Change safely
//        if (incoming.getDepartment() != null && incoming.getDepartment().getRecordId() != null) {
//            UUID newDeptId = incoming.getDepartment().getRecordId();
//            if (existing.getDepartment() == null || !existing.getDepartment().getRecordId().equals(newDeptId)) {
//                Department newDept = departmentDao.findById(newDeptId).orElseThrow();
//                existing.setDepartment(newDept); // JPA manages the rest thanks to helper methods
//            }
//        } else if (incoming.getDepartment() == null) {
//            existing.setDepartment(null);
//        }
//
//        return repository.saveAndFlush(existing);
//    }
	public Employee moveEmployee(UUID empId, UUID deptId) {
        Employee emp = repository.findById(empId).orElseThrow();
        Department dept = departmentDao.findById(deptId).orElseThrow();
        emp.setDepartment(dept);
        return repository.saveAndFlush(emp);
    }
}
