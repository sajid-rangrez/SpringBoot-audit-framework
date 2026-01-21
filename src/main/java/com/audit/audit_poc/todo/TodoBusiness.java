package com.audit.audit_poc.todo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoBusiness {

    private final TodoDao dao;

    public TodoBusiness(TodoDao dao) {
        this.dao = dao;
    }

    public List<Todo> findAll() {
        return dao.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    public Todo save(Todo todo) {
        return dao.save(todo);
    }

    public Todo update(UUID id, Todo incoming) {
        Todo existing = dao.findById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
        
        // Update fields
        existing.setTask(incoming.getTask());
        existing.setPriority(incoming.getPriority());
        existing.setCompleted(incoming.isCompleted());
        
        return dao.save(existing);
    }

    public void deleteById(UUID id) {
        dao.deleteById(id);
    }
}