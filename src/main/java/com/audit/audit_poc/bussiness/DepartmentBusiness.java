package com.audit.audit_poc.bussiness;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.repository.AbsDao;
import com.audit.audit_poc.repository.DepartmentDao;

@Service
public class DepartmentBusiness extends AbsBusiness<Department> {
    
    private final DepartmentDao departmentDao;

    public DepartmentBusiness(DepartmentDao dao) {
        this.departmentDao = dao;
    }

    @Override
    protected AbsDao<Department> getDao() {
        return departmentDao;
    }
    public void deleteById(UUID id) {
        // 1. Fetch the Department
        Department dept = departmentDao.findById(id).orElseThrow();

        // 2. Manually clear employees (This triggers safe Orphan Removal)
        if (dept.getEmployees() != null) {
            dept.getEmployees().clear();
        }
        
        // 3. Save/Flush to apply the employee removal FIRST
        departmentDao.saveAndFlush(dept); 

        // 4. Now delete the empty department
        departmentDao.delete(dept);
    }
    
    @Transactional
    public Department update(UUID id, Department incomingData) {
        Department existingDept = departmentDao.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
       
        existingDept.setName(incomingData.getName());
        
        return departmentDao.save(existingDept);
    }
}