package com.audit.audit_poc.employee;

import org.springframework.stereotype.Service;

import com.audit.audit_poc.abs.AbsService;

@Service
public class DepartmentService extends AbsService<Department, DepartmentDao>{

	public DepartmentService(DepartmentDao repository) {
		super(repository);
		// TODO Auto-generated constructor stub
	}
	
//    public Department update(UUID id, Department incoming) {
//        Department existing = repository.findById(id).orElseThrow();
//        existing.setName(incoming.getName());
//        return repository.saveAndFlush(existing);
//    }


}
