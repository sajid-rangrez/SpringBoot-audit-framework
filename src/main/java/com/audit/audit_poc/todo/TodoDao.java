package com.audit.audit_poc.todo;

import org.springframework.stereotype.Repository;

import com.audit.audit_poc.abs.AbsDao;

@Repository
public interface TodoDao extends AbsDao<Todo> {
    // You can add findByTask etc. if needed
}
