package com.audit.audit_poc.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.entity.Employee;
import com.audit.audit_poc.repository.DepartmentRepository;
import com.audit.audit_poc.repository.EmployeeRepository;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentController(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    // 1. Create Parent (Department)
    @PostMapping
    public Department createDepartment(@RequestBody Department department) {
        return departmentRepository.save(department);
    }

    // 2. Add Child (Employee) to Parent
    @PostMapping("/{deptId}/employees")
    public Employee addEmployee(@PathVariable Long deptId, @RequestBody Employee employee) {
        Department dept = departmentRepository.findById(deptId).orElseThrow();
        employee.setDepartment(dept);
        return employeeRepository.save(employee);
    }

    // 3. Move Employee to new Department (Test Relationship Update)
    @PutMapping("/employees/{empId}/move/{newDeptId}")
    public Employee moveEmployee(@PathVariable Long empId, @PathVariable Long newDeptId) {
        Employee emp = employeeRepository.findById(empId).orElseThrow();
        Department newDept = departmentRepository.findById(newDeptId).orElseThrow();
        
        emp.setDepartment(newDept); // This triggers the Audit Update
        return employeeRepository.save(emp);
    }
}