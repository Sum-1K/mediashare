package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.ContentDao;
import com.example.demo.dao.ReelDao;
import com.example.demo.model.Content;
import com.example.demo.model.Reel;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReelController {

    private final ContentDao contentDao;
    private final ReelDao reelDao;

    @Autowired
    public ReelController(ContentDao contentDao, ReelDao reelDao) {
        this.contentDao = contentDao;
        this.reelDao = reelDao;
    }

    @PostMapping("/reels")
    public String uploadReel(@RequestParam("videoFile") MultipartFile videoFile,
                             @RequestParam(required = false) String caption,
                             HttpSession session) {

        try {
            // 1️⃣ Get logged-in user
            User user = (User) session.getAttribute("loggedInUser");
            if (user == null) {
                return "redirect:/users/login";
            }

            // 2️⃣ Insert into content table
            Content content = new Content();
            content.setUserId(user.getUser_id());
            content.setCreatedAt(LocalDateTime.now());
            Long contentId = contentDao.saveAndReturnId(content);

            // 3️⃣ Save video file
            String uploadDir = "src/main/resources/static/uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.write(path, videoFile.getBytes());
            String videoPath = "src/main/resources/static/uploads/" + fileName;

            // 4️⃣ Insert into reel table
            Reel reel = new Reel();
            reel.setReelId(contentId); // PK = content_id
            reel.setCaption(caption);
            reel.setVideoFile(videoPath);
            reelDao.save(reel);

            // 5️⃣ Redirect to profile
            return "redirect:/profile";

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/profile?error";
        }
    }
}
