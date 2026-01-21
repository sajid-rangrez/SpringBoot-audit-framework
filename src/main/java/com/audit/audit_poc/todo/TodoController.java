package com.audit.audit_poc.todo;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoBusiness business;

    public TodoController(TodoBusiness business) {
        this.business = business;
    }

    @GetMapping
    public List<Todo> getAll() {
        return business.findAll();
    }

    @PostMapping
    public Todo create(@RequestBody Todo todo) {
        todo.setRecordId(null); // Ensure new
        return business.save(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable UUID id, @RequestBody Todo todo) {
        return business.update(id, todo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        business.deleteById(id);
    }
}