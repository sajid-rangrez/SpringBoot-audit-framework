package com.audit.audit_poc.webcontroller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder; // Import this
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audit.audit_poc.AppUser;
import com.audit.audit_poc.AppUserDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDao appUserDao;        // <--- Add this
    private final PasswordEncoder passwordEncoder; // <--- Add this

    // Inject the new dependencies
    public AuthController(AuthenticationManager authenticationManager, 
                          AppUserDao appUserDao, 
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.appUserDao = appUserDao;
        this.passwordEncoder = passwordEncoder;
    }

    // Inside the login method...
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        // 1. Authenticate
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password"))
        );

        // 2. Set Context
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // 3. Create Session
        HttpSession session = request.getSession(true);
        
        // 4. *** CRITICAL FIX: Manually save the security context to the session ***
        // This forces Spring to write the session attribute NOW, ensuring the cookie is valid.
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return ResponseEntity.ok(Map.of(
            "username", auth.getName(),
            "sessionId", session.getId(),
            "message", "Login Successful"
        ));
    }
    // --- NEW REGISTER ENDPOINT ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String companyId = payload.get("companyId");

        // 1. Validation
        if (appUserDao.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        // 2. Create User
        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // Hash password
        newUser.setCompanyId(companyId);
        newUser.setRole("USER"); // Default role

        appUserDao.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out");
    }
}