package com.example.demo.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

@PostMapping("/stories/archive")
@ResponseBody
public String toggleArchive(@RequestParam("storyId") Long storyId,
                            @RequestParam("archived") boolean archived) {
    try {
        Story story = storyDao.findById(storyId);
        if (story == null) {
            return "Story not found";
        }
        story.setIsArchived(archived);
        storyDao.update(story);
        return archived ? "Story archived" : "Story unarchived";
    } catch (Exception e) {
        e.printStackTrace();
        return "Error updating archive status";
    }
}


@GetMapping("/stories/status/{id}")
@ResponseBody
public Map<String, Object> getStoryStatus(@PathVariable("id") Long id) {
    Map<String, Object> response = new HashMap<>();
    try {
        Story story = storyDao.findById(id);
        if (story != null) {
            response.put("isArchived", story.getIsArchived());
        } else {
            response.put("isArchived", false);
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.put("isArchived", false);
    }
    return response;
}

@PostMapping("/stories/highlight")
@ResponseBody
public String toggleHighlight(@RequestParam Long storyId, 
                              @RequestParam boolean isHighlighted) {
    Story story = storyDao.findById(storyId);
    if (story == null) return "Story not found";

    story.setIsHighlighted(isHighlighted);
    storyDao.update(story);

    return "Success";
}






   
}
