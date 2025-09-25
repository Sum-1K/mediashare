package com.example.demo.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private final ContentDao contentDao;
    private final StoryDao storyDao;

    public StoryController(ContentDao contentDao, StoryDao storyDao) {
        this.contentDao = contentDao;
        this.storyDao = storyDao;
    }
    @PostMapping("/stories")
public String uploadStory(@RequestParam("storyFile") MultipartFile file,
                          @RequestParam("highlightTopic") String highlightTopic,
                          HttpSession session) {
    try {
        // get logged-in user
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login"; // user not logged in
        }

        String uploadDir = "src/main/resources/static/uploads/";
        Files.createDirectories(Paths.get(uploadDir));

        Path path = Paths.get(uploadDir).resolve(file.getOriginalFilename());
        Files.write(path, file.getBytes());

        // save content with logged-in user
        Content content = new Content();
        content.setUserId(user.getUser_id());
        Long contentId = contentDao.saveAndReturnId(content);

        // save story
        Story story = new Story();
        story.setStoryId(contentId);
        story.setHighlightTopic(highlightTopic);
        story.setMediaFile("/uploads/" + file.getOriginalFilename());
        story.setIsArchived(false);  // <-- add this
        story.setIsHighlighted(false);    // <-- add this
        storyDao.save(story);

        return "redirect:/home";

    } catch (Exception e) {
        e.printStackTrace();
        return "error";
    }
}

   
}
