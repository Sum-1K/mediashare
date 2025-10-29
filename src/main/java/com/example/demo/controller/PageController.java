package com.example.demo.controller;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dao.FollowDao;
import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.ReelDao;
import com.example.demo.dao.StoryDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.BlockedUser;
import com.example.demo.model.Media;
import com.example.demo.model.Post;
import com.example.demo.model.Reel;
import com.example.demo.model.Story;
import com.example.demo.model.User;
import com.example.demo.dao.BlockedUserDao;

import jakarta.servlet.http.HttpSession;


@Controller
public class PageController {

    @Autowired
    private PostDao postDao; // DAO for Post entity

    @Autowired
    private ReelDao reelDao; // DAO for Reel entity

    @Autowired
    private FollowDao followDao; // DAO for followers/following

    @Autowired
    private MediaDao mediaDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private StoryDao storyDao;


    @Autowired
    private BlockedUserDao blockedUserDao;


    //handler methods to handle /abc request
    @GetMapping("/")
    public String home() {
        return "homeDefault"; // Thymeleaf will render login.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "home"; // Thymeleaf will render login.html
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // Thymeleaf template: chat.html in templates/
    }
    
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("currentUser", user); // Keep this line for follow buttons

        // Fetch posts by user
        int postCount = postDao.countByUserId(user.getUser_id());
        int reelCount = reelDao.countByUserId(user.getUser_id()); 
        model.addAttribute("postCount", postCount + reelCount);

        // Fetch followers and following count
        int followers = followDao.countFollowers(user.getUser_id()); // people who follow this user
        int following = followDao.countFollowing(user.getUser_id()); // people this user follows
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);

        // Fetch posts
        List<Post> posts = postDao.findByUserId(user.getUser_id());

        // Map each post -> its media list
        Map<Long, List<Media>> postMediaMap = new HashMap<>();
        for (Post post : posts) {
            List<Media> mediaList = mediaDao.findByPostId(post.getPostId());

            // Convert filesystem path to web path
            for (Media media : mediaList) {
                String fileName = Paths.get(media.getUrl()).getFileName().toString();
                media.setUrl("/uploads/" + fileName);
            }

            postMediaMap.put(post.getPostId(), mediaList);
        }

       
        model.addAttribute("posts", posts);
        model.addAttribute("postMediaMap", postMediaMap);

        List<Reel> reels = reelDao.findByUserId(user.getUser_id());
    model.addAttribute("reels", reels);

        model.addAttribute("timestamp", System.currentTimeMillis());
        // Optional: posts and media
        // model.addAttribute("posts", postDao.findByUserId(user.getUser_id()));
        // model.addAttribute("postMediaMap", mediaDao.findByUserId(user.getUser_id()));
        
            // Fetch all highlighted stories for current user
List<Story> highlightedStories = storyDao.findHighlightedStoriesByUser(user.getUser_id());

// Convert filesystem path to web path
for (Story story : highlightedStories) {
    String fileName = Paths.get(story.getMediaFile()).getFileName().toString();
    story.setMediaFile("/uploads/" + fileName);
}

model.addAttribute("highlightedStories", highlightedStories);

    
        model.addAttribute("canViewPosts", true);
        // 🟢 Add these to prevent Thymeleaf null errors:
    model.addAttribute("isBlockedByMe", false);
    model.addAttribute("hasBlockedMe", false);

        return "profile";


}


    @GetMapping("/settings")
public String settingsPage(HttpSession session, Model model) {
    // Get current user
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        return "redirect:/users/login"; // redirect if not logged in
    }

    model.addAttribute("user", user);
    return "settings"; // Thymeleaf template: settings.html
}


    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications"; // notifications.html in templates/
    }

    @GetMapping("/profile/{profileId}")
public String viewProfile(@PathVariable Long profileId, Model model, HttpSession session) {
    // Get currently logged-in user from session
    User loggedInUser = (User) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
        return "redirect:/login"; // or show an error
    }
    
    User profileUser = userDao.findById(profileId); // or userDao.findById(profileId)
    if (profileUser == null) { // ✅ handle missing user
        model.addAttribute("error", "User not found.");
        model.addAttribute("canViewPosts", false); // always set
        return "errorPage";
    }

    Long loggedInUserId = loggedInUser.getUser_id(); // adjust based on your User class
        // Check if logged-in user is following this profile
    User.Privacy privacy = profileUser.getPrivacy();
    boolean isFollowing = followDao.isFollowing(loggedInUserId, profileId);
    
     // ✅ Check block status both ways
    boolean isBlockedByMe = blockedUserDao.exists(loggedInUserId, profileId); // you blocked them
    boolean hasBlockedMe = blockedUserDao.exists(profileId, loggedInUserId);  // they blocked you

    if (privacy == User.Privacy.PRIVATE && !isFollowing && !loggedInUserId.equals(profileId)) {
    model.addAttribute("error", "You must follow this user to see their posts.");
    return "errorPage";
}

    // Fetch posts by user
        int postCount = postDao.countByUserId(profileId);
        int reelCount = reelDao.countByUserId(profileId); 
        model.addAttribute("postCount", postCount + reelCount);

        // Fetch followers and following count
        int followers = followDao.countFollowers(profileId); // people who follow this user
        int following = followDao.countFollowing(profileId); // people this user follows
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
    

        // ✅ Block toggle option
        model.addAttribute("isBlockedByMe", isBlockedByMe);
        model.addAttribute("hasBlockedMe", hasBlockedMe);


    // Fetch posts and reels for the profile user
    // Fetch posts
        List<Post> posts = postDao.findByUserId(profileId);

        // Map each post -> its media list
        Map<Long, List<Media>> postMediaMap = new HashMap<>();
        for (Post post : posts) {
            List<Media> mediaList = mediaDao.findByPostId(post.getPostId());

            // Convert filesystem path to web path
            for (Media media : mediaList) {
                String fileName = Paths.get(media.getUrl()).getFileName().toString();
                media.setUrl("/uploads/" + fileName);
            }

            postMediaMap.put(post.getPostId(), mediaList);
        }

       
        model.addAttribute("posts", posts);
        model.addAttribute("postMediaMap", postMediaMap);

        List<Reel> reels = reelDao.findByUserId(profileId);
    model.addAttribute("reels", reels);

        model.addAttribute("timestamp", System.currentTimeMillis());
        // Optional: posts and media
        // model.addAttribute("posts", postDao.findByUserId(user.getUser_id()));
        // model.addAttribute("postMediaMap", mediaDao.findByUserId(user.getUser_id()));

        // Fetch the profile user's full info (the person being visited)

model.addAttribute("user", profileUser);
model.addAttribute("currentUser", loggedInUser);

      

        // Add:
        // Fetch all highlighted stories for profile user
List<Story> highlightedStories = storyDao.findHighlightedStoriesByUser(profileUser.getUser_id());

// Convert filesystem path to web path
for (Story story : highlightedStories) {
    String fileName = Paths.get(story.getMediaFile()).getFileName().toString();
    story.setMediaFile("/uploads/" + fileName);
}

model.addAttribute("highlightedStories", highlightedStories);

 boolean canViewPosts = ((profileUser.getPrivacy() == User.Privacy.PUBLIC)
                           || isFollowing
                           || loggedInUserId.equals(profileId)) && !isBlockedByMe && !hasBlockedMe;
    model.addAttribute("canViewPosts", canViewPosts);



      return "profile";

}


}