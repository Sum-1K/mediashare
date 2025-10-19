package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.CommentDao;
import com.example.demo.dao.ContentDao;
import com.example.demo.dao.LikeDao;
import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.model.Comment;
import com.example.demo.dto.CommentDTO;
import com.example.demo.model.Content;
import com.example.demo.model.Media;
import com.example.demo.model.Media.MediaType;
import com.example.demo.model.Post;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {

    private final ContentDao contentDao;
    private final PostDao postDao;
    private final MediaDao mediaDao;
    private final LikeDao likeDao;
    private final CommentDao commentDao;

    @Autowired
    public PostController(ContentDao contentDao, PostDao postDao, MediaDao mediaDao, CommentDao commentDao, LikeDao likeDao) {
        this.contentDao = contentDao;
        this.postDao = postDao;
        this.mediaDao = mediaDao;
        this.commentDao = commentDao;
        this.likeDao = likeDao;
    }

    @PostMapping("/posts")
    public String uploadPost(@RequestParam("mediaFiles") List<MultipartFile> mediaFiles,
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

            // 3️⃣ Insert into post table
            Post post = new Post();
            post.setPostId(contentId);
            post.setCaption(caption);
            postDao.save(post);

            // 4️⃣ Handle multiple media files
            String uploadDir = "src/main/resources/static/uploads/"; // relative to project root
            Files.createDirectories(Paths.get(uploadDir));

            int order = 1; // maintain order
            for (MultipartFile file : mediaFiles) {
                if (!file.isEmpty()) {
                    // Save file
                    Path path = Paths.get(uploadDir, file.getOriginalFilename());
                    Files.write(path, file.getBytes());
                    String mediaPath = "src/main/resources/static/uploads/" + file.getOriginalFilename(); // path to use in HTML

                    // Detect type
                    String contentType = file.getContentType();
                    MediaType mediaType;
                    if (contentType != null && contentType.startsWith("image")) {
                        mediaType = MediaType.PHOTO;
                    } else if (contentType != null && contentType.startsWith("video")) {
                        mediaType = MediaType.VIDEO;
                    } else {
                        mediaType = MediaType.PHOTO; // fallback
                    }

                    // Insert into media table
                    Media media = new Media();
                    media.setPostId(contentId);
                    media.setUrl(mediaPath);
                    media.setType(mediaType);
                    media.setMediaOrder(order++);
                    mediaDao.insert(media);
                }
            }

            // 5️⃣ Redirect to user's profile
            return "redirect:/profile"; // you can also do "/profile/" + user.getUsername()

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/profile?error"; // optional error flag
        }
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
    Post post = postDao.findById(id);
    if (post == null) {
        throw new RuntimeException("Post not found");
    }

     // ✅ Fetch associated media
    List<Media> mediaList = mediaDao.findByPostId(id);
    System.out.println("MediaList for post " + id + ": " + mediaList);

    // Get number of likes for this post
    int likesCount = likeDao.countByContentId(post.getPostId());

    List<CommentDTO> comments = commentDao.findWithUsernameByContentId(id);
    model.addAttribute("comments", comments);


    model.addAttribute("post", post);
    model.addAttribute("mediaList", mediaList); // ✅ add this line
    model.addAttribute("likesCount", likesCount); // pass separately
    model.addAttribute("comments", comments);
    return "postDetail";  // Thymeleaf template
}

}
