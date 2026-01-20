package com.audit.audit_poc.webcontroller;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.audit.audit_poc.audit.AuditLogDao;
import com.audit.audit_poc.bussiness.DepartmentBusiness;
import com.audit.audit_poc.bussiness.EmployeeBusiness;
import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.entity.Employee;

@Controller
public class WebController {

    private final DepartmentBusiness departmentBusiness;
    private final EmployeeBusiness employeeBusiness;
    private final AuditLogDao auditLogDao;

    public WebController(DepartmentBusiness departmentBusiness, EmployeeBusiness employeeBusiness, AuditLogDao auditLogDao) {
        this.departmentBusiness = departmentBusiness;
        this.employeeBusiness = employeeBusiness;
        this.auditLogDao = auditLogDao;
    }

    @GetMapping("/")
    public String viewAuditLogs(Model model) {
        model.addAttribute("logs", auditLogDao.findAll(Sort.by(Sort.Direction.DESC, "createdDate")));
        return "audit-logs";
    }

    @GetMapping("/departments")
    public String viewDepartments(Model model) {
        model.addAttribute("departments", departmentBusiness.findAll());
        model.addAttribute("newDepartment", new Department());
        return "departments";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@ModelAttribute Department department) {
        // 1. HERE: department.recordId is NULL
        
        Department savedDept = departmentBusiness.save(department);
        
        // 2. HERE: savedDept.recordId will have a UUID (e.g., "550e8400-e29b...")
        System.out.println("Generated ID: " + savedDept.getRecordId());
        
        return "redirect:/departments";
    }

    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        model.addAttribute("employees", employeeBusiness.findAll());
        model.addAttribute("departments", departmentBusiness.findAll());
        model.addAttribute("newEmployee", new Employee());
        return "employees";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeBusiness.save(employee);
        return "redirect:/employees";
    }
 // Inside FrameworkWebController.java

    @GetMapping("/employees/edit/{id}")
    public String editEmployee(@PathVariable UUID id, Model model) {
        // 1. Fetch the employee to edit
        Employee employeeToEdit = employeeBusiness.findById(id);

        // 2. Put it in the model as "newEmployee" so the form populates with its data
        model.addAttribute("newEmployee", employeeToEdit);

        // 3. Reload the rest of the page data (List of all employees & departments)
        model.addAttribute("employees", employeeBusiness.findAll());
        model.addAttribute("departments", departmentBusiness.findAll());
        
        return "employees"; // Return the same view
    }

    // Note: RequestParam is now UUID
    @PostMapping("/employees/move")
    public String moveEmployee(@RequestParam UUID employeeId, @RequestParam UUID departmentId) {
        Employee emp = employeeBusiness.findById(employeeId);
        Department dept = departmentBusiness.findById(departmentId);
        emp.setDepartment(dept);
        employeeBusiness.save(emp);
        return "redirect:/employees";
    }
}