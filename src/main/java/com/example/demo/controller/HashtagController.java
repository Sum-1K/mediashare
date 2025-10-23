package com.example.demo.controller;
import com.example.demo.dao.*;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HashtagController {
     @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private ContentHashtagDao contentHashtagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private MediaDao mediaDao;

    @Autowired
    private ContentDao contentDao;

    @GetMapping("/hashtag/{text}")
    public String viewHashtagPosts(@PathVariable("text") String text, Model model) {
        // 1️⃣ Find the hashtag by text
        Hashtag hashtag;
        try {
            hashtag = hashtagDao.findByText(text);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Hashtag not found: #" + text);
            return "hashtagPosts";
        }

        // 2️⃣ Find all content IDs linked to this hashtag
        List<Long> contentIds = contentHashtagDao.findContentIdsByHashtagId(hashtag.getHashtagId());
        if (contentIds.isEmpty()) {
            model.addAttribute("hashtagText", text);
            model.addAttribute("posts", Collections.emptyList());
            return "hashtagPosts";
        }

        // 3️⃣ Fetch all posts for those content IDs
        List<Map<String, Object>> posts = new ArrayList<>();

        for (Long contentId : contentIds) {
            Post post = postDao.findById(contentId);
            if (post == null) continue;

            // Get first media as preview (image/video)
            List<Media> mediaList = mediaDao.findByPostId(contentId);
            String previewUrl = mediaList.isEmpty() ? "/uploads/default.jpg" : mediaList.get(0).getUrl();

            // Get creation date
            Content content = contentDao.findById(contentId);

            Map<String, Object> postData = new HashMap<>();
            postData.put("id", post.getPostId());
            postData.put("caption", post.getCaption());
            postData.put("preview", previewUrl);
            postData.put("createdAt", content != null ? content.getCreatedAt() : null);

            posts.add(postData);
        }

        // 4️⃣ Sort posts in reverse chronological order
        posts = posts.stream()
                .sorted((a, b) -> {
                    LocalDateTime d1 = (LocalDateTime) a.get("createdAt");
                    LocalDateTime d2 = (LocalDateTime) b.get("createdAt");
                    if (d1 == null || d2 == null) return 0;
                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());

        // 5️⃣ Add to model
        model.addAttribute("hashtagText", text);
        model.addAttribute("posts", posts);

        return "hashtagPosts"; // -> Thymeleaf view
    }
}
