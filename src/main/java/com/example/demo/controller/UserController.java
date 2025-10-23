package com.example.demo.controller;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dao.FollowDao;
import com.example.demo.dao.FollowRequestDao;
import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.ReelDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Media;
import com.example.demo.model.Post;
import com.example.demo.model.Reel;
import com.example.demo.model.User;
import com.example.demo.service.FollowService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private FollowRequestDao followRequestDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private MediaDao mediaDao;

    @Autowired
    private ReelDao reelDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //✅ Show signup form
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // ✅ Handle registration (form submission)
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        System.out.println("Received user: " + user);
        System.out.println("Received password: " + user.getPassword());
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "signup"; // your signup page
        }

        if (user.getUser_name() == null || user.getPassword() == null || user.getEmail() == null) {
            model.addAttribute("error", "All fields are required");
            return "signup";
        }

        if (userDao.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "signup";
        }

        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);
        if (user.getJoin_date() == null) {
            user.setJoin_date(LocalDateTime.now());
        }

        userDao.save(user);
        return "redirect:/users/login"; // ✅ go to login page after successful signup
    }

    // ✅ Show login page
    @GetMapping("/users/login")
    public String loginPage() {
        return "homeDefault";
    }

    @PostMapping("/users/login")
    public String loginUser(@RequestParam String user_name,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        Optional<User> optionalUser = userDao.findByUserName(user_name);
        if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            model.addAttribute("error", "Invalid credentials");
            return "homeDefault";
        }

        // store logged-in user in session
        User loggedInUser = optionalUser.get();
        session.setAttribute("loggedInUser", loggedInUser);
        session.setAttribute("user", loggedInUser); // For compatibility with follow system

        return "redirect:/home"; // redirect to home or dashboard
    }

    // ✅ View other users' profiles
    @GetMapping("/user/{userId}")
    public String viewUserProfile(@PathVariable Long userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        User profileUser = userDao.findById(userId);
        
        if (profileUser == null) {
            return "redirect:/dashboard";
        }
        
        // Get post count for the profile user
        int postCount = postDao.countByUserId(userId);
        int reelCount = reelDao.countByUserId(userId); // get reel count too (ReelDao has this method)

        // Get follower and following counts
        int followerCount = followDao.getFollowerCount(userId);
        int followingCount = followDao.getFollowingCount(userId);
        
        // Fetch posts by this profile user
        List<Post> posts = postDao.findByUserId(userId);

        // Build post -> media map (so template can show thumbnails)
        Map<Long, List<Media>> postMediaMap = new HashMap<>();
        for (Post post : posts) {
            List<Media> mediaList = mediaDao.findByPostId(post.getPostId());
            // convert filesystem path to web path (same as PageController)
            for (Media media : mediaList) {
                String fileName = Paths.get(media.getUrl()).getFileName().toString();
                media.setUrl("/uploads/" + fileName);
            }
            postMediaMap.put(post.getPostId(), mediaList);
        }

        // Fetch reels by this profile user
        List<Reel> reels = reelDao.findByUserId(userId);

        model.addAttribute("user", profileUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("postCount", postCount + reelCount); // total items (posts + reels)
        model.addAttribute("followers", followerCount);
        model.addAttribute("following", followingCount);
        model.addAttribute("posts", posts);
        model.addAttribute("postMediaMap", postMediaMap);
        model.addAttribute("reels", reels);
                
        return "profile";
    }

    // ✅ Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInUser");
        session.removeAttribute("user");
        session.invalidate();
        return "redirect:/users/login";
    }

    // ✅ Search users
    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "query", required = false) String query, 
                            HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        if (query != null && !query.trim().isEmpty()) {
            // Search users by username or name (you'll need to implement this in UserDao)
            // List<User> searchResults = userDao.searchUsers(query);
            // model.addAttribute("searchResults", searchResults);
            model.addAttribute("query", query);
        }

        model.addAttribute("currentUser", currentUser);
        return "search"; // Create a search.html template
    }

    @GetMapping("/followers/{userId}")
public String viewFollowers(@PathVariable Long userId, HttpSession session, Model model) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    User profileUser = userDao.findById(userId);

    if (currentUser == null || profileUser == null) {
        return "redirect:/users/login";
    }

    List<User> followers = followService.getFollowers(profileUser.getUser_id());
    List<UserDTO> followerDTOs = new ArrayList<>();
for (User u : followers) {
    boolean blocked = followService.isBlocked(currentUser.getUser_id(), u.getUser_id()) ||
                      followService.isBlocked(u.getUser_id(), currentUser.getUser_id());
    boolean closeFriend = followService.isCloseFriend(currentUser.getUser_id(), u.getUser_id());

    followerDTOs.add(new UserDTO(u.getUser_id(), u.getUser_name(), closeFriend, blocked, u.getPhoto()));
}

   // Use same attribute name as 'followingList' for consistency
    model.addAttribute("followersList", followerDTOs);
    model.addAttribute("currentUser", currentUser);
    model.addAttribute("profileUser", profileUser);
    model.addAttribute("title", "Followers");
    model.addAttribute("isFollowersPage", true);

    return "followers"; 
}


@GetMapping("/following/{userId}")
public String viewFollowing(@PathVariable Long userId, HttpSession session, Model model) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    User profileUser = userDao.findById(userId);

    if (currentUser == null || profileUser == null) {
        return "redirect:/users/login";
    }

    // Fetch the users that profileUser is following
    List<User> following = followService.getFollowing(profileUser.getUser_id());

    // Build DTOs with blocked and close friend info
    List<UserDTO> followingDTOs = new ArrayList<>();
for (User u : following) {
    boolean blocked = followService.isBlocked(currentUser.getUser_id(), u.getUser_id()) ||
                      followService.isBlocked(u.getUser_id(), currentUser.getUser_id());
    boolean closeFriend = followService.isCloseFriend(currentUser.getUser_id(), u.getUser_id());

    followingDTOs.add(new UserDTO(u.getUser_id(), u.getUser_name(), closeFriend, blocked, u.getPhoto()));
}

    model.addAttribute("followingList", followingDTOs);
    model.addAttribute("currentUser", currentUser);
    model.addAttribute("profileUser", profileUser);
    model.addAttribute("title", "Following");
    model.addAttribute("isFollowersPage", false);

    return "following";
}



    // ✅ Update user profile
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, 
                               HttpSession session, 
                               Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        // Update the current user with new data
        currentUser.setFirst_name(updatedUser.getFirst_name());
        currentUser.setLast_name(updatedUser.getLast_name());
        currentUser.setBio(updatedUser.getBio());
        currentUser.setPrivacy(updatedUser.getPrivacy());
        
        // Update in database
        userDao.update(currentUser);
        
        // Update session
        session.setAttribute("loggedInUser", currentUser);
        session.setAttribute("user", currentUser);
        
        model.addAttribute("message", "Profile updated successfully");
        return "redirect:/profile";
    }

    @PostMapping("/block/{userId}")
public String blockUser(@PathVariable Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    if (currentUser != null) {
        followService.blockUser(currentUser.getUser_id(), userId);
    }
    return "redirect:/followers/" + currentUser.getUser_id(); // or redirect back to referring page
}

@PostMapping("/unblock/{userId}")
public String unblockUser(@PathVariable Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    if (currentUser != null) {
        followService.unblockUser(currentUser.getUser_id(), userId);
    }
    return "redirect:/followers/" + currentUser.getUser_id();
}

@PostMapping("/addCloseFriend/{userId}")
public String addCloseFriend(@PathVariable Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    if (currentUser != null) {
        followService.addCloseFriend(currentUser.getUser_id(), userId);
    }
    return "redirect:/followers/" + currentUser.getUser_id();
}

@PostMapping("/removeCloseFriend/{userId}")
public String removeCloseFriend(@PathVariable Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    if (currentUser != null) {
        followService.removeCloseFriend(currentUser.getUser_id(), userId);
    }
    return "redirect:/followers/" + currentUser.getUser_id();
}


}