package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dao.StoryDao;
import com.example.demo.model.Story;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private StoryDao storyDao;

   @GetMapping( {"/home"})
public String home(Model model, HttpSession session) {
    User loggedInUser = (User) session.getAttribute("loggedInUser");
    List<Story> stories = storyDao.findActiveStories();

    model.addAttribute("stories", stories);
    model.addAttribute("loggedInUser", loggedInUser);

    return "home";
}

}
