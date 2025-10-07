package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import com.example.demo.service.ChatService;
import com.example.demo.model.Chat;
import com.example.demo.model.User;

import com.example.demo.dao.UserDao;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserDao userDao;

    @GetMapping("/chat/users")
    @ResponseBody
    public List<User> getChatUsers(HttpSession session) {
        System.out.println("/chat/users endpoint was called");
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            System.out.println("loggedInUser is null");
            return List.of();
        }

        return chatService.getChatUsers(currentUser.getUser_id());
    }

    @GetMapping("/chat/{userId}")
public String openChat(@PathVariable Long userId, Model model, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");
    Long currentUserId = currentUser.getUser_id();

    // get messages between current user and userId
    List<Chat> messages = chatService.getMessagesBetween(currentUserId, userId);

    // get user info for display
    User chatUser = userDao.findById(userId);

    model.addAttribute("messages", messages);
    model.addAttribute("chatUser", chatUser);
    model.addAttribute("currentUserId", currentUserId);

    return "chat"; // Thymeleaf template (chat.html)
}

@GetMapping("/chat/messages/{userId}")
@ResponseBody
public List<Chat> getMessages(@PathVariable Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");

    // Check if user is logged in
    if (currentUser == null) {
        return new ArrayList<>(); // return empty list if not logged in
    }

    Long currentUserId = currentUser.getUser_id();

    // fetch messages between the two users
    List<Chat> messages = chatService.getMessagesBetween(currentUserId, userId);
    return messages != null ? messages : new ArrayList<>();
}

@PostMapping("/chat/send/{receiverId}")
@ResponseBody
public ResponseEntity<String> sendMessage(@PathVariable Long receiverId, @RequestParam String message, HttpSession session) {
    User sender = (User) session.getAttribute("loggedInUser");

    // Check if user is logged in
    if (sender == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
    
    chatService.saveMessage(sender.getUser_id(), receiverId, message);
    return ResponseEntity.ok("Message sent successfully");
}


}
