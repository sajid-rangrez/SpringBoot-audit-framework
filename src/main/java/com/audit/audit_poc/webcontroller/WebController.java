package com.audit.audit_poc.webcontroller;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.audit.audit_poc.AuditLogRepository;
import com.audit.audit_poc.entity.Department;
import com.audit.audit_poc.entity.Employee;
import com.audit.audit_poc.repository.DepartmentRepository;
import com.audit.audit_poc.repository.EmployeeRepository;

@Controller // Note: @Controller, NOT @RestController
public class WebController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogRepository auditLogRepository;

    public WebController(DepartmentRepository departmentRepository, 
                         EmployeeRepository employeeRepository, 
                         AuditLogRepository auditLogRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // --- HOME / AUDIT LOGS ---
    @GetMapping("/")
    public String viewAuditLogs(Model model) {
        // Fetch logs sorted by latest first
        model.addAttribute("logs", auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
        return "audit-logs";
    }

    // --- DEPARTMENTS ---
    @GetMapping("/departments")
    public String viewDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("newDepartment", new Department());
        return "departments";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@ModelAttribute Department department) {
        departmentRepository.save(department);
        return "redirect:/departments";
    }

    // --- EMPLOYEES ---
    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll()); // For the dropdown
        model.addAttribute("newEmployee", new Employee());
        return "employees";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    // --- MOVE EMPLOYEE (CHANGE DEPT) ---
    @PostMapping("/employees/move")
    public String moveEmployee(@RequestParam Long employeeId, @RequestParam Long departmentId) {
        Employee emp = employeeRepository.findById(employeeId).orElseThrow();
        Department dept = departmentRepository.findById(departmentId).orElseThrow();
        
        emp.setDepartment(dept); // This triggers the Audit Update
        employeeRepository.save(emp);
        
        return "redirect:/employees";
    }
    
    // --- DELETE EMPLOYEE ---
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}