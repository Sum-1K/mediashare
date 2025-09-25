package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserDao userDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //✅ Show signup form
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // ✅ Handle registration (form submission)
    @PostMapping("/register")
public String registerUser(@ModelAttribute User user, @RequestParam("confirmPassword") String confirmPassword, Model model) {
    System.out.println("Received user: " + user);
    System.out.println("Received password: " + user.getPassword());
    if (!user.getPassword().equals(confirmPassword)) {
        model.addAttribute("error", "Passwords do not match");
        return "signup"; // your signup page
    }

    if (user.getUser_name() == null || user.getPassword() == null || user.getEmail() == null) {
        model.addAttribute("error", "All fields are required");
        return "signup";
    }

    if (userDao.existsByEmail(user.getEmail())) {
        model.addAttribute("error", "Email already exists");
        return "signup";
    }

    String hashed = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashed);
    if (user.getJoin_date() == null) {
        user.setJoin_date(LocalDateTime.now());
    }

    userDao.save(user);
    return "redirect:/users/login"; // ✅ go to login page after successful signup
}


    // ✅ Show login page
    @GetMapping("/users/login")
    public String loginPage() {
        return "homeDefault";
    }

    @PostMapping("/users/login")
public String loginUser(@RequestParam String user_name,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
    Optional<User> optionalUser = userDao.findByUserName(user_name);
    if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
        model.addAttribute("error", "Invalid credentials");
        return "homeDefault";
    }

    // store logged-in user in session
    session.setAttribute("loggedInUser", optionalUser.get());

    return "redirect:/dashboard"; // redirect to home or dashboard
}

}
