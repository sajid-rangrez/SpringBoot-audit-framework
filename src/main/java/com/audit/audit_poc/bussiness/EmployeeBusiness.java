package com.audit.audit_poc.bussiness;
import org.springframework.stereotype.Service;

import com.audit.audit_poc.entity.Employee;
import com.audit.audit_poc.repository.AbsDao;
import com.audit.audit_poc.repository.EmployeeDao;

@Service
public class EmployeeBusiness extends AbsBusiness<Employee> {

    private final EmployeeDao dao;

    public EmployeeBusiness(EmployeeDao dao) {
        this.dao = dao;
    }

    @Override
    protected AbsDao<Employee> getDao() {
        return dao;
    }
}