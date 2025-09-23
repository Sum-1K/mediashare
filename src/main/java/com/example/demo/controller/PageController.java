package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "homeDefault"; // Thymeleaf will render login.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "homeDefault"; // optional, for explicit /login URL
    }
}
