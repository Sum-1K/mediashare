package com.example.demo.controller;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dao.FollowDao;
import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.ReelDao;
import com.example.demo.model.Media;
import com.example.demo.model.Post;
import com.example.demo.model.Reel;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;


@Controller
public class PageController {

    @Autowired
    private PostDao postDao; // DAO for Post entity

    @Autowired
    private ReelDao reelDao; // DAO for Reel entity

    @Autowired
    private FollowDao followDao; // DAO for followers/following

    @Autowired
    private MediaDao mediaDao;

    //handler methods to handle /abc request
    @GetMapping("/")
    public String home() {
        return "homeDefault"; // Thymeleaf will render login.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "home"; // Thymeleaf will render login.html
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // Thymeleaf template: chat.html in templates/
    }
    
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("currentUser", user); // Keep this line for follow buttons

        // Fetch posts by user
        int postCount = postDao.countByUserId(user.getUser_id());
        int reelCount = reelDao.countByUserId(user.getUser_id()); 
        model.addAttribute("postCount", postCount + reelCount);

        // Fetch followers and following count
        int followers = followDao.countFollowers(user.getUser_id()); // people who follow this user
        int following = followDao.countFollowing(user.getUser_id()); // people this user follows
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);

        // Fetch posts
        List<Post> posts = postDao.findByUserId(user.getUser_id());

        // Map each post -> its media list
        Map<Long, List<Media>> postMediaMap = new HashMap<>();
        for (Post post : posts) {
            List<Media> mediaList = mediaDao.findByPostId(post.getPostId());

            // Convert filesystem path to web path
            for (Media media : mediaList) {
                String fileName = Paths.get(media.getUrl()).getFileName().toString();
                media.setUrl("/uploads/" + fileName);
            }

            postMediaMap.put(post.getPostId(), mediaList);
        }

       
        model.addAttribute("posts", posts);
        model.addAttribute("postMediaMap", postMediaMap);

        List<Reel> reels = reelDao.findByUserId(user.getUser_id());
    model.addAttribute("reels", reels);


        // Optional: posts and media
        // model.addAttribute("posts", postDao.findByUserId(user.getUser_id()));
        // model.addAttribute("postMediaMap", mediaDao.findByUserId(user.getUser_id()));

        return "profile";
}


    @GetMapping("/settings")
    public String settingsPage() {
        return "settings"; // settings.html in templates/
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications"; // notifications.html in templates/
    }
}