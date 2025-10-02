package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
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
import com.example.demo.dao.PostDao;
import com.example.demo.dao.UserDao;
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

        return "redirect:/dashboard"; // redirect to home or dashboard
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
        
        // Get follower and following counts
        int followerCount = followDao.getFollowerCount(userId);
        int followingCount = followDao.getFollowingCount(userId);
        
        model.addAttribute("user", profileUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("postCount", postCount);
        model.addAttribute("followers", followerCount);
        model.addAttribute("following", followingCount);
        
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

    // ✅ Followers list
    @GetMapping("/followers/{userId}")
    public String viewFollowers(@PathVariable Long userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        User profileUser = userDao.findById(userId);
        
        if (currentUser == null || profileUser == null) {
            return "redirect:/users/login";
        }
        
        List<User> followers = followDao.getFollowers(userId);
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("users", followers);
        model.addAttribute("title", "Followers");
        model.addAttribute("isFollowersPage", true);
        
        return "follow-list"; // Create a follow-list.html template
    }

    // ✅ Following list
    @GetMapping("/following/{userId}")
    public String viewFollowing(@PathVariable Long userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        User profileUser = userDao.findById(userId);
        
        if (currentUser == null || profileUser == null) {
            return "redirect:/users/login";
        }
        
        List<User> following = followDao.getFollowing(userId);
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("users", following);
        model.addAttribute("title", "Following");
        model.addAttribute("isFollowersPage", false);
        
        return "follow-list"; // Create a follow-list.html template
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
}