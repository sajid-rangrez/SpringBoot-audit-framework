package com.audit.audit_poc.security;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.audit.audit_poc.AppUser;
import com.audit.audit_poc.AppUserDao;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserDao appUserDao;

    public CustomUserDetailsService(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // We embed the Company ID into the UserDetails object so we can grab it later easily
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
    }
    
    // Helper to get the actual AppUser entity if needed
    public AppUser getAppUser(String username) {
        return appUserDao.findByUsername(username).orElse(null);
    }
}