package com.example.demo.controller;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // REGISTER USER
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getUser_name() == null || user.getPassword() == null || user.getEmail() == null) {
            return ResponseEntity.badRequest().body("Username, email, and password are required");
        }

        // Check if email already exists
        if (userDao.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        // Hash the password
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);

        // Set join date to now if not provided
        if (user.getJoin_date() == null) {
            user.setJoin_date(LocalDateTime.now());
        }

        Long id = userDao.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered with ID: " + id);
    }

    // LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        if ((loginRequest.getUser_name() == null && loginRequest.getEmail() == null) || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username/email and password are required");
        }

        Optional<User> optionalUser;
        if (loginRequest.getUser_name() != null) {
            optionalUser = userDao.findByUserName(loginRequest.getUser_name());
        } else {
            optionalUser = userDao.findByUserName(loginRequest.getEmail());
        }

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        user.setPassword(null); // remove password before sending to frontend
        return ResponseEntity.ok(user);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userDao.findById(id);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
