package com.audit.audit_poc.controller;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.bussiness.DepartmentBusiness;
import com.audit.audit_poc.entity.Department;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
	
    private final DepartmentBusiness business;

    public DepartmentController(DepartmentBusiness business) {
        this.business = business;
    }

    @GetMapping
    public List<Department> getAll() {
        return business.findAll();
    }

    @PostMapping
    public Department save(@RequestBody Department department) {
        return business.save(department);
    }

    @PutMapping("/{id}")
    public Department update(@PathVariable UUID id, @RequestBody Department department) {
        // Pass to the new update method
        return business.update(id, department);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        business.deleteById(id);
    }
}