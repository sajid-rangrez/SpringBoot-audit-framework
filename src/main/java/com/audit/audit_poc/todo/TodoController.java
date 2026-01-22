package com.audit.audit_poc.todo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.abs.AbsController;

@RestController
@RequestMapping("/api/todos")
public class TodoController extends AbsController<Todo, TodoService> {
    public TodoController(TodoService service) {
        super(service);
    }
}