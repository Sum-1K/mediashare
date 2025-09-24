package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.ContentDao;
import com.example.demo.dao.StoryDao;
import com.example.demo.model.Content;
import com.example.demo.model.Story;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class StoryController {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private StoryDao storyDao;

    @PostMapping("/stories")
    public String addStory(@RequestParam("storyFile") MultipartFile file,
                           @RequestParam(required = false) String highlightTopic,
                           HttpSession session) throws IOException {

        // 1. Get logged in user
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // no session → login first
        }

        // 2. Save uploaded file to static/uploads
        String uploadDir = "src/main/resources/static/uploads/";
        File saveFile = new File(uploadDir + file.getOriginalFilename());
        file.transferTo(saveFile);
        String mediaPath = "/uploads/" + file.getOriginalFilename();

        // 3. Insert into content table & get generated content_id
        Content content = new Content();
        content.setUserId(user.getUser_id());
        content.setCreatedAt(LocalDateTime.now());

        Long contentId = contentDao.saveAndReturnId(content);

        // 4. Insert into stories table
        Story story = new Story();
        story.setStoryId(contentId); // PK = FK
        story.setMediaFile(mediaPath);
        story.setHighlightTopic(highlightTopic);
        story.setIsHighlighted(false);
        story.setIsArchived(false);
        storyDao.save(story);

        // 5. Redirect back to home
        return "redirect:/home";
    }
}
