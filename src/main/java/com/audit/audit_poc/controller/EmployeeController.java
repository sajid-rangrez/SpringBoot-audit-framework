package com.audit.audit_poc.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.bussiness.DepartmentBusiness;
import com.audit.audit_poc.bussiness.EmployeeBusiness;
import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.entity.Employee;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeBusiness empBusiness;
    private final DepartmentBusiness deptBusiness;

    public EmployeeController(EmployeeBusiness empBusiness, DepartmentBusiness deptBusiness) {
        this.empBusiness = empBusiness;
        this.deptBusiness = deptBusiness;
    }

    @GetMapping
    public List<Employee> getAll() {
        return empBusiness.findAll();
    }

    @PostMapping
    public Employee save(@RequestBody Employee employee) {
        return empBusiness.save(employee);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        empBusiness.deleteById(id);
    }
    

    @PutMapping("/{id}")
    public Employee update(@PathVariable UUID id, @RequestBody Employee employee) {
        // Pass to the new update method
        return empBusiness.update(id, employee);
    }
    @PostMapping("/move")
    public Employee move(@RequestBody Map<String, String> payload) {
        UUID empId = UUID.fromString(payload.get("employeeId"));
        UUID deptId = UUID.fromString(payload.get("departmentId"));
        
        Employee emp = empBusiness.findById(empId);
        Department newDept = deptBusiness.findById(deptId);
        
        emp.setDepartment(newDept);
        
        return empBusiness.save(emp);
    }
}