package com.example.demo.controller;

import com.example.demo.dao.UserDao;
import com.example.demo.model.BlockedUser;
import com.example.demo.model.User;
import com.example.demo.service.BlockedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/blocked")
public class BlockedUserController {

    @Autowired
    private BlockedUserService blockedUserService;

    @Autowired
    private UserDao userDao;


    // 🧱 Show all blocked users (Manage Blocked)
    @GetMapping("/my")
    public String viewBlockedUsers(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) return "redirect:/login";

        List<BlockedUser> blockedList = blockedUserService.getBlockedUsers(loggedInUser.getUser_id());
        List<User> blockedUsers = blockedList.stream()
        .map(b -> userDao.findById(b.getBlockedToId()))
        .collect(Collectors.toList());


        model.addAttribute("blockedUsers", blockedUsers);
        model.addAttribute("user", loggedInUser);
        return "blocked.html";
    }

    // 🚫 Block a user
    @PostMapping("/block/{id}")
    @ResponseBody
    public String blockUser(@PathVariable("id") Long blockedId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) return "NOT_LOGGED_IN";

        blockedUserService.blockUser(loggedInUser.getUser_id(), blockedId);
        return "BLOCKED";
    }

    // ✅ Unblock a user
    @PostMapping("/unblock/{id}")
    @ResponseBody
    public String unblockUser(@PathVariable("id") Long blockedId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) return "NOT_LOGGED_IN";

        blockedUserService.unblockUser(loggedInUser.getUser_id(), blockedId);
        return "UNBLOCKED";
    }

    
}
