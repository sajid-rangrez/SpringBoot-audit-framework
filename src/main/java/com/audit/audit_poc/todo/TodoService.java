package com.audit.audit_poc.todo;
import org.springframework.stereotype.Service;
import com.audit.audit_poc.abs.AbsService;

@Service
public class TodoService extends AbsService<Todo, TodoDao> {
    public TodoService(TodoDao repository) {
        super(repository);
    }
}