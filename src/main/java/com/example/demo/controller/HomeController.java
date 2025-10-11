package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.dao.FollowDao;
import com.example.demo.dao.StoryDao;
import com.example.demo.model.Story;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private StoryDao storyDao;

    @Autowired
    private FollowDao followDao;

    @GetMapping({"/home"})
    public String home(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("currentUser", currentUser);

        // Fetch current user's stories
        List<Story> userStories = storyDao.findActiveStoriesByUser(currentUser.getUser_id());
        model.addAttribute("userStories", userStories);
        logger.info("User Stories for {} (ID: {}): {}", currentUser.getUser_name(), currentUser.getUser_id(), userStories);

        // Fetch stories of users the current user is following
        List<User> followingUsers = followDao.getFollowing(currentUser.getUser_id());
        List<Story> followingStories = new ArrayList<>();
        for (User user : followingUsers) {
            List<Story> stories = storyDao.findActiveStoriesByUser(user.getUser_id());
            followingStories.addAll(stories);
        }
        model.addAttribute("followingStories", followingStories);
        logger.info("Following Users for {} (ID: {}): {}", currentUser.getUser_name(), currentUser.getUser_id(), followingUsers);
        logger.info("Following Stories for {} (ID: {}): {}", currentUser.getUser_name(), currentUser.getUser_id(), followingStories);

        return "home";
    }
}