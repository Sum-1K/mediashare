package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dao.StoryDao;
import com.example.demo.model.Story;

@Controller
public class HomeController {

    @Autowired
    private StoryDao storyDao;

    @GetMapping("/home")
    public String home(Model model) {
        // Fetch all active stories to show on the homepage
        List<Story> stories = storyDao.findActiveStories();
        model.addAttribute("stories", stories);

        return "home"; // Thymeleaf template: home.html
    }
}
