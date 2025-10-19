package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dao.CommentDao;
import com.example.demo.dao.ContentDao;
import com.example.demo.dao.FollowDao;
import com.example.demo.dao.LikeDao;
import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.ReelDao;
import com.example.demo.dao.StoryDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.Comment;
import com.example.demo.model.Content;
import com.example.demo.model.Media;
import com.example.demo.model.Post;
import com.example.demo.model.Reel;
import com.example.demo.model.Story;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final StoryDao storyDao;
    private final FollowDao followDao;
    private final PostDao postDao;
    private final ReelDao reelDao;
    private final UserDao userDao;
    private final ContentDao contentDao;
    private final LikeDao likeDao;
    private final CommentDao commentDao;
    private final MediaDao mediaDao;

    public HomeController(StoryDao storyDao, FollowDao followDao, PostDao postDao, ReelDao reelDao,
                          UserDao userDao, ContentDao contentDao, LikeDao likeDao,
                          CommentDao commentDao, MediaDao mediaDao) {
        this.storyDao = storyDao;
        this.followDao = followDao;
        this.postDao = postDao;
        this.reelDao = reelDao;
        this.userDao = userDao;
        this.contentDao = contentDao;
        this.likeDao = likeDao;
        this.commentDao = commentDao;
        this.mediaDao = mediaDao;
    }

    @GetMapping({"/home"})
    public String home(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("currentUser", currentUser);

        // Fetch current user's stories
        List<Story> userStories = storyDao.findActiveStoriesByUser(currentUser.getUser_id());
        model.addAttribute("userStories", userStories);
        logger.info("User Stories for {} (ID: {}): {}", currentUser.getUser_name(), currentUser.getUser_id(), userStories);

        // Fetch stories of users the current user is following
        List<User> followingUsers = followDao.getFollowing(currentUser.getUser_id());
        List<Long> followingIds = followingUsers.stream().map(User::getUser_id).toList();

        List<Object[]> followingStories = storyDao.findActiveStoriesWithUsersByUserIds(followingIds);
        model.addAttribute("followingStories", followingStories);

        logger.info("Following Users for {} (ID: {}): {}", currentUser.getUser_name(), currentUser.getUser_id(), followingUsers);
        logger.info("Following Stories for {} (ID: {}): {}", currentUser.getUser_name(), currentUser.getUser_id(), followingStories);

        // Fetch posts and reels for current user and followed users
        List<Long> allUserIds = new ArrayList<>();
        allUserIds.add(currentUser.getUser_id());
        allUserIds.addAll(followingIds);

        List<Post> allPosts = new ArrayList<>();
        for (Long userId : allUserIds) {
            List<Post> userPosts = postDao.findByUserId(userId);
            allPosts.addAll(userPosts);
        }

        List<Reel> allReels = new ArrayList<>();
        for (Long userId : allUserIds) {
            List<Reel> userReels = reelDao.findByUserId(userId);
            allReels.addAll(userReels);
        }

        // Map posts and reels data
        Map<Long, User> postUserMap = new HashMap<>();
        Map<Long, Date> postCreatedAtMap = new HashMap<>();
        Map<Long, List<Media>> postMediaMap = new HashMap<>();
        Map<Long, Integer> postLikesMap = new HashMap<>();
        Map<Long, List<Comment>> postCommentsMap = new HashMap<>();
        for (Post post : allPosts) {
            Content content = contentDao.findById(post.getPostId());
            if (content != null) {
                User postUser = userDao.findById(content.getUserId());
                postUserMap.put(post.getPostId(), postUser);
                LocalDateTime createdAt = content.getCreatedAt();
                Date createdAtDate = createdAt != null ? java.sql.Timestamp.valueOf(createdAt) : new Date();
                postCreatedAtMap.put(post.getPostId(), createdAtDate);
                postMediaMap.put(post.getPostId(), mediaDao.findByPostId(post.getPostId()));
                postLikesMap.put(post.getPostId(), likeDao.countByContentId(post.getPostId()));
                postCommentsMap.put(post.getPostId(), commentDao.findByContentId(post.getPostId()));
            }
        }

        Map<Long, User> reelUserMap = new HashMap<>();
        Map<Long, Date> reelCreatedAtMap = new HashMap<>();
        Map<Long, Integer> reelLikesMap = new HashMap<>();
        Map<Long, List<Comment>> reelCommentsMap = new HashMap<>();
        for (Reel reel : allReels) {
            Content content = contentDao.findById(reel.getReelId());
            if (content != null) {
                User reelUser = userDao.findById(content.getUserId());
                reelUserMap.put(reel.getReelId(), reelUser);
                LocalDateTime createdAt = content.getCreatedAt();
                Date createdAtDate = createdAt != null ? java.sql.Timestamp.valueOf(createdAt) : new Date();
                reelCreatedAtMap.put(reel.getReelId(), createdAtDate);
                reelLikesMap.put(reel.getReelId(), likeDao.countByContentId(reel.getReelId()));
                reelCommentsMap.put(reel.getReelId(), commentDao.findByContentId(reel.getReelId()));
            }
        }

        // Create comment usernames map
        Map<Long, String> commentUsernamesMap = new HashMap<>();
        for (List<Comment> comments : postCommentsMap.values()) {
            for (Comment comment : comments) {
                User user = userDao.findById(comment.getUserId());
                if (user != null) {
                    commentUsernamesMap.put(comment.getCommentId(), user.getUser_name());
                } else {
                    commentUsernamesMap.put(comment.getCommentId(), "Unknown");
                }
            }
        }
        for (List<Comment> comments : reelCommentsMap.values()) {
            for (Comment comment : comments) {
                User user = userDao.findById(comment.getUserId());
                if (user != null) {
                    commentUsernamesMap.put(comment.getCommentId(), user.getUser_name());
                } else {
                    commentUsernamesMap.put(comment.getCommentId(), "Unknown");
                }
            }
        }
        model.addAttribute("commentUsernamesMap", commentUsernamesMap);
        logger.info("Comment Usernames Map: {}", commentUsernamesMap);

        // Combine posts and reels into a single feed
        List<Map<String, Object>> feedItems = new ArrayList<>();
        for (Post post : allPosts) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "POST");
            item.put("content", post);
            item.put("createdAt", postCreatedAtMap.get(post.getPostId()));
            feedItems.add(item);
        }
        for (Reel reel : allReels) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "REEL");
            item.put("content", reel);
            item.put("createdAt", reelCreatedAtMap.get(reel.getReelId()));
            feedItems.add(item);
        }

        // Sort feed items by createdAt (descending)
        feedItems.sort((item1, item2) -> {
            Date date1 = (Date) item1.get("createdAt");
            Date date2 = (Date) item2.get("createdAt");
            return date2 != null && date1 != null ? date2.compareTo(date1) : 0;
        });

        model.addAttribute("feedItems", feedItems);
        model.addAttribute("postUserMap", postUserMap);
        model.addAttribute("postCreatedAtMap", postCreatedAtMap);
        model.addAttribute("postMediaMap", postMediaMap);
        model.addAttribute("postLikesMap", postLikesMap);
        model.addAttribute("postCommentsMap", postCommentsMap);
        model.addAttribute("reelUserMap", reelUserMap);
        model.addAttribute("reelCreatedAtMap", reelCreatedAtMap);
        model.addAttribute("reelLikesMap", reelLikesMap);
        model.addAttribute("reelCommentsMap", reelCommentsMap);

        return "home";
    }
}