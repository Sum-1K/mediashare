package com.example.demo.controller;

import java.io.File;
import java.io.IOException; // your DAO class for DB operations

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private UserDao userDao;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/profile/";

    @PostMapping("/updateProfilePic")
public String updateProfilePic(@RequestParam("profilePic") MultipartFile file,
                               HttpSession session,
                               Model model) throws IOException {

    User user = (User) session.getAttribute("loggedInUser"); // fetch user directly
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

    return "settings";
}

}
