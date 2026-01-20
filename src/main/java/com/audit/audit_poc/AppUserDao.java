package com.audit.audit_poc;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.audit.audit_poc.repository.AbsDao;

@Repository
public interface AppUserDao extends AbsDao<AppUser> {
    Optional<AppUser> findByUsername(String username);
}