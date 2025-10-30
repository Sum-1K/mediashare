package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.SettingsDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.Settings;
import com.example.demo.dao.StoryDao;
import com.example.demo.model.Story;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SettingsDao settingsDao;

    
    @Autowired
    private StoryDao storyDao;  // <-- Add this injection

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/profile/";

    // Add this method - it serves the settings page

    @PostMapping("/updateProfilePic")
    public String updateProfilePic(@RequestParam("profilePic") MultipartFile file,
                                   HttpSession session,
                                   Model model) throws IOException {

        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && !file.isEmpty()) {

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Delete old profile picture if exists and not default
            if (user.getPhoto() != null && !user.getPhoto().equals("default_dp.jpg")) {
                File oldFile = new File(UPLOAD_DIR + user.getPhoto());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // Save new profile picture
            String originalFilename = file.getOriginalFilename();
            String newFilename = "profile_" + user.getUser_id() + "_" + originalFilename;
            File dest = new File(UPLOAD_DIR + newFilename);
            dest.getParentFile().mkdirs();
            file.transferTo(dest);

            // Update user in DB
            user.setPhoto(newFilename);
            userDao.updatePhoto(user);

            // Update session attribute
            session.setAttribute("loggedInUser", user);

            model.addAttribute("message", "Profile picture updated successfully!");
        }

        // Always add user to model for Thymeleaf
        model.addAttribute("user", user);

        return "redirect:/profile";
    }

    @PostMapping("/updatePrivacy")
    @ResponseBody
    public String updatePrivacy(@RequestParam("privacy") String privacy, HttpSession session) {
        try {
            // Get the logged-in user from session
            User user = (User) session.getAttribute("loggedInUser");
            if (user == null) {
                return "ERROR: User not logged in";
            }

            // Convert to enum
            User.Privacy privacyEnum = User.Privacy.valueOf(privacy.toUpperCase());

            // Update in DB
            userDao.updatePrivacy(user.getUser_id(), privacyEnum);

            // Update session object
            user.setPrivacy(privacyEnum);
            session.setAttribute("loggedInUser", user);

        return "SUCCESS";
    } catch (Exception e) {
        e.printStackTrace();
        return "ERROR: " + e.getMessage();
    }
}

    @GetMapping("/archive")
public String viewArchive(HttpSession session, Model model) {
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        return "redirect:/login"; // Redirect if user is not logged in
    }

    // Fetch user's archived stories (you can later connect this with your Story DAO)
    // Example: List<Story> archivedStories = storyDao.findArchivedByUserId(user.getUser_id());
    // model.addAttribute("stories", archivedStories);

    // For now, let's just send user info
    // Fetch archived stories for this user
    List<Story> archivedStories = storyDao.findArchivedStoriesByUser(user.getUser_id());

    model.addAttribute("user", user);
    model.addAttribute("stories", archivedStories);
    return "archive_story"; // Thymeleaf will load archive_story.html
}


    @PostMapping("/updateTheme")
@ResponseBody
public String updateTheme(@RequestParam("theme") String theme, HttpSession session) {
    try {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "ERROR: User not logged in";
        }

        // Get or create settings for user
        List<Settings> userSettings = settingsDao.findByUserId(user.getUser_id());
        Settings settings;
        
        if (userSettings.isEmpty()) {
            // Create new settings
            settings = new Settings();
            settings.setUserId(user.getUser_id());
            settings.setTheme(theme);
            settings.setStoryArchivedEnable(true);
            settings.setAllowTags(true);
            settings.setNotificationTags(true);
            settings.setCreatedAt(LocalDateTime.now());
            settingsDao.insert(settings);
        } else {
            // Update existing settings
            settings = userSettings.get(0);
            settings.setTheme(theme);
            settingsDao.update(settings);
        }

        // Update session immediately
        session.setAttribute("userTheme", theme);
        System.out.println("Theme updated to: " + theme + " for user: " + user.getUser_id());
        
        return "SUCCESS";
    } catch (Exception e) {
        e.printStackTrace();
        return "ERROR: " + e.getMessage();
    }
}

    @ModelAttribute("userTheme")
public String getCurrentTheme(HttpSession session) {
    User user = (User) session.getAttribute("loggedInUser");
    if (user != null) {
        // First check session
        String sessionTheme = (String) session.getAttribute("userTheme");
        if (sessionTheme != null) {
            return sessionTheme;
        }
        
        // Then check database
        List<Settings> userSettings = settingsDao.findByUserId(user.getUser_id());
        if (!userSettings.isEmpty()) {
            String theme = userSettings.get(0).getTheme();
            // Store in session for future use
            session.setAttribute("userTheme", theme);
            return theme;
        }
        
        // Initialize with default and save to database
        String defaultTheme = "light";
        Settings settings = new Settings();
        settings.setUserId(user.getUser_id());
        settings.setTheme(defaultTheme);
        settings.setStoryArchivedEnable(true);
        settings.setAllowTags(true);
        settings.setNotificationTags(true);
        settings.setCreatedAt(LocalDateTime.now());
        settingsDao.insert(settings);
        
        session.setAttribute("userTheme", defaultTheme);
        return defaultTheme;
    }
    return "light"; // default theme
}
}