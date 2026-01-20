package com.audit.audit_poc.bussiness;

import org.springframework.stereotype.Service;

import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.repository.AbsDao;
import com.audit.audit_poc.repository.DepartmentDao;

@Service
public class DepartmentBusiness extends AbsBusiness<Department> {
    
    private final DepartmentDao dao;

    public DepartmentBusiness(DepartmentDao dao) {
        this.dao = dao;
    }

    @Override
    protected AbsDao<Department> getDao() {
        return dao;
    }
}