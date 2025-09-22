package com.example.demo.controller;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    @PostMapping("/add")
    public Long addUser(@RequestBody User user) {
        if (user.getJoin_date() == null) {
            user.setJoin_date(LocalDateTime.now());
        }
        return userDao.save(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userDao.findById(id);
    }
}
