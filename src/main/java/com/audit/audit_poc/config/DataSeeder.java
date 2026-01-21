package com.audit.audit_poc.config;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.audit.audit_poc.AppUser;
//import com.audit.audit_poc.AppUserDao;
//
//@Configuration
//public class DataSeeder {
//
//    @Bean
//    public CommandLineRunner seedUsers(AppUserDao dao, PasswordEncoder encoder) {
//        return args -> {
//            if (dao.count() == 0) {
//                AppUser admin = new AppUser();
//                admin.setUsername("admin");
//                admin.setPassword(encoder.encode("admin123")); // Password is "admin123"
//                admin.setRole("ADMIN");
//                admin.setCompanyId("COMP-001"); // Default Company ID
//                dao.save(admin);
//                System.out.println("Default User Created: admin / admin123");
//            }
//        };
//    }
//}


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.audit.audit_poc.AppUser;
import com.audit.audit_poc.AppUserDao;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedUsers(AppUserDao dao, PasswordEncoder encoder) {
        return args -> {
            // Check by Username, not by count()
            if (dao.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setCompanyId("COMP-001");
                
                dao.save(admin);
                System.out.println("✅ Default User Created: admin / admin123");
            } else {
                System.out.println("ℹ️ Admin user already exists. Skipping seed.");
            }
        };
    }
}