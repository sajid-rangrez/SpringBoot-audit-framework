package com.audit.audit_poc.employee;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.abs.AbsController;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController extends AbsController<Employee, EmployeeService>{

	public EmployeeController(EmployeeService service) {
		super(service);
	}
//
//	@Override
//	@PutMapping("/{id}")
//    public ResponseEntity<Employee> update(@PathVariable UUID id, @RequestBody Employee emp) {
//        return ResponseEntity.ok(service.update(id, emp));
//    }
//    
    @PostMapping("/move")
    public Employee move(@RequestBody Map<String, String> payload) {
        UUID empId = UUID.fromString(payload.get("employeeId"));
        UUID deptId = UUID.fromString(payload.get("departmentId"));
        return service.moveEmployee(empId, deptId);
    }
}
