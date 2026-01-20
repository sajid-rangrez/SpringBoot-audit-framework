package com.audit.audit_poc.webcontroller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String login() {
        return "login"; // Points to login.html
    }
}