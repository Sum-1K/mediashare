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
import com.example.demo.dao.ContentHashtagDao;
import com.example.demo.dao.HashtagDao;
import com.example.demo.dao.LikeDao;
import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.TagDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dao.FollowDao;
import com.example.demo.model.Comment;
import com.example.demo.dto.CommentDTO;
import com.example.demo.model.Content;
import com.example.demo.model.ContentHashtag;
import com.example.demo.model.Hashtag;
import com.example.demo.model.Tag;
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
    private final HashtagDao hashtagDao;
    private final ContentHashtagDao contentHashtagDao;
    private final TagDao tagDao;
    private final UserDao userDao;
    private final FollowDao followDao;

    @Autowired
    public PostController(ContentDao contentDao, PostDao postDao, MediaDao mediaDao, CommentDao commentDao, LikeDao likeDao, HashtagDao hashtagDao, ContentHashtagDao contentHashtagDao, TagDao tagDao, UserDao userDao, FollowDao followDao) {
        this.contentDao = contentDao;
        this.postDao = postDao;
        this.mediaDao = mediaDao;
        this.commentDao = commentDao;
        this.likeDao = likeDao;
        this.hashtagDao = hashtagDao;
        this.contentHashtagDao = contentHashtagDao;
        this.tagDao = tagDao;
        this.userDao=userDao;
        this.followDao=followDao;
    }

    @PostMapping("/posts")
    public String uploadPost(@RequestParam("mediaFiles") List<MultipartFile> mediaFiles,
                            @RequestParam(required = false) String caption,
                            @RequestParam(required = false) List<Long> taggedUserIds,
                            HttpSession session) {

        try {
            System.out.println("Upload endpoint hit!");
            System.out.println("Received taggedUsers: " + taggedUserIds);
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

            if (caption != null && !caption.isEmpty()) {
            // Use regex to find hashtags
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("#(\\w+)");
            java.util.regex.Matcher matcher = pattern.matcher(caption);

            while (matcher.find()) {
                String hashtagText = matcher.group(1).toLowerCase();

                Long hashtagId = null;
                try {
                    Hashtag existing = hashtagDao.findByText(hashtagText);
                    hashtagId = existing.getHashtagId();
                } catch (Exception e) {
                    // Not found → insert new one
                    Hashtag newTag = new Hashtag();
                    newTag.setText(hashtagText);
                    hashtagDao.insert(newTag);

                    // Retrieve ID of inserted hashtag
                    Hashtag inserted = hashtagDao.findByText(hashtagText);
                    hashtagId = inserted.getHashtagId();
                }

                // Link content ↔ hashtag
                if (hashtagId != null) {
                    ContentHashtag contentHashtag=new ContentHashtag();
                    contentHashtag.setHashtag_id(hashtagId);
                    contentHashtag.setContent_id(contentId);
                    contentHashtagDao.save(contentHashtag);
                }
            }
        }

        // --- TAGGED USERS ---
if (taggedUserIds != null && !taggedUserIds.isEmpty()) {
    for (Long taggedId : taggedUserIds) {
        User taggedUser = userDao.findById(taggedId); // find the user
        if (taggedUser == null) continue;

        boolean canTag = false;

        // Check tagging rules
        if (taggedUser.getPrivacy() == User.Privacy.PUBLIC) {
            canTag = true;
        } else if (taggedUser.getPrivacy() == User.Privacy.PRIVATE) {
            canTag = followDao.isFollowing(user.getUser_id(), taggedId);
        }

        if (canTag) {
            Tag tag = new Tag();
            tag.setUser_id(taggedId);
            tag.setContent_id(content.getContentId());
            tag.setStatus("PENDING"); // default status
            tagDao.save(tag);          // insert into DB

            // Optional: create notification
            //notificationDao.createTagNotification(taggedId, content.getContentId(), user.getUser_id());
        }
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

    model.addAttribute("post", post);
    model.addAttribute("mediaList", mediaList); // ✅ add this line
    model.addAttribute("likesCount", likesCount); // pass separately
    model.addAttribute("comments", comments);
    return "postDetail";  // Thymeleaf template
}

}
