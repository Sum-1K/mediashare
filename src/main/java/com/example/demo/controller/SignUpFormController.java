package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.model.User;
import jakarta.servlet.http.HttpSession;

@Controller
public class SignUpFormController {
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, HttpSession session) {
        // Add your user registration logic here
        // Save user to database
        // Set session attributes
        session.setAttribute("userId", user.getUser_id());
        session.setAttribute("username", user.getUser_name());
        
        return "redirect:/home";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "redirect:/homeDefault.html";
    }
}