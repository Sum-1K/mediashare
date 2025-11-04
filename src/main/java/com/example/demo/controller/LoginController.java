package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password, 
                       HttpSession session) {
        // Add your authentication logic here
        // Validate username and password against database
        
        // If authentication successful:
        // User user = userDao.findByUsername(username);
        // session.setAttribute("userId", user.getUser_id());
        // session.setAttribute("username", user.getUser_name());
        
        return "redirect:/home";
    }
}