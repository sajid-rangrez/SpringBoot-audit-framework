package com.audit.audit_poc.bussiness;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.entity.Employee;
import com.audit.audit_poc.repository.AbsDao;
import com.audit.audit_poc.repository.DepartmentDao;
import com.audit.audit_poc.repository.EmployeeDao;

@Service
public class EmployeeBusiness extends AbsBusiness<Employee> {

    private final EmployeeDao employeeDao;
    
    private final DepartmentDao departmentDao;

    public EmployeeBusiness(EmployeeDao employeeDao, DepartmentDao departmentDao) {
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
    }

    @Override
    protected AbsDao<Employee> getDao() {
        return employeeDao;
    }
    
    @Transactional
    public Employee update(UUID id, Employee incomingData) {
        // 1. Fetch Existing
        Employee existingEmp = employeeDao.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 2. Update Simple Fields
        existingEmp.setName(incomingData.getName());
        existingEmp.setRole(incomingData.getRole());

        // 3. Handle Department Change (The tricky part)
        Department incomingDept = incomingData.getDepartment();
        Department oldDept = existingEmp.getDepartment();

        // Case A: User wants to REMOVE the department
        if (incomingDept == null) {
            existingEmp.setDepartment(null);
            if (oldDept != null) {
                oldDept.getEmployees().remove(existingEmp); // Sync Old Dept
            }
        }
        // Case B: User wants to CHANGE/SET the department
        else if (incomingDept.getRecordId() != null) {
            UUID newDeptId = incomingDept.getRecordId();
            
            // Only fetch DB if the ID is actually different
            boolean isDifferent = (oldDept == null) || !oldDept.getRecordId().equals(newDeptId);

            if (isDifferent) {
                // Remove from old parent list
                if (oldDept != null) {
                    oldDept.getEmployees().remove(existingEmp);
                }

                // Add to new parent list
                Department newDept = departmentDao.findById(newDeptId)
                        .orElseThrow(() -> new RuntimeException("New Department not found"));
                
                existingEmp.setDepartment(newDept);
                newDept.getEmployees().add(existingEmp); // Sync New Dept
            }
        }

        // 4. Save
        return employeeDao.save(existingEmp);
    }
}