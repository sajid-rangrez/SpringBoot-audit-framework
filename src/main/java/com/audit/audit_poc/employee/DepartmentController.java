package com.audit.audit_poc.employee;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.abs.AbsController;

@RestController
@RequestMapping("/api/department")
public class DepartmentController extends AbsController<Department, DepartmentService>{

	public DepartmentController(DepartmentService service) {
		super(service);
		// TODO Auto-generated constructor stub
	}
	
//	@Override
//	@PutMapping("/{id}")
//    public ResponseEntity<Department> update(@PathVariable UUID id, @RequestBody Department dept) {
//        return ResponseEntity.ok(service.update(id, dept));
//    }

}
