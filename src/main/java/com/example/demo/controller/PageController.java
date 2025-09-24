package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    //handler methods to handle /abc request
    @GetMapping("/")
    public String home() {
        return "homeDefault"; // Thymeleaf will render login.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "home"; // Thymeleaf will render login.html
    }

    // @GetMapping("/login")
    // public String loginPage() {
    //     return "homeDefault"; // optional, for explicit /login URL
    // }

    // @GetMapping("/signup")
    // public String signupPage() {
    //     return "signup"; // optional, for explicit /login URL
    // }
}
