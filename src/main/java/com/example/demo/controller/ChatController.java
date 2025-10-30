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
import com.example.demo.dto.ChatMessage;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;

import com.example.demo.service.NotificationService;

@Controller
public class ChatController {
    
    private ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Autowired
    private UserDao userDao;

    @Autowired
    private NotificationService notificationService;

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
    List<ChatMessage> messages = chatService.getMessagesBetween(currentUserId, userId);

    // get user info for display
    User chatUser = userDao.findById(userId);

    model.addAttribute("messages", messages);
    model.addAttribute("chatUser", chatUser);
    model.addAttribute("currentUserId", currentUserId);

    return "chat"; // Thymeleaf template (chat.html)
}

@GetMapping("/chat/messages/{userId}")
@ResponseBody
public List<ChatMessage> getMessages(@PathVariable Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("loggedInUser");

    // Check if user is logged in
    if (currentUser == null) {
        return new ArrayList<>(); // return empty list if not logged in
    }

    Long currentUserId = currentUser.getUser_id();

    // fetch messages between the two users
    List<ChatMessage> messages = chatService.getMessagesBetween(currentUserId, userId);
    return messages != null ? messages : new ArrayList<>();
}

// @PostMapping("/chat/send/{receiverId}")
// @ResponseBody
// public ResponseEntity<String> sendMessage(@PathVariable Long receiverId, @RequestParam String message, HttpSession session) {
//     User sender = (User) session.getAttribute("loggedInUser");

//     // Check if user is logged in
//     if (sender == null) {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
//     }
    
//     chatService.saveMessage(sender.getUser_id(), receiverId, message);
//     return ResponseEntity.ok("Message sent successfully");
// }

    // @MessageMapping("/sendMessage")
    // public void sendMessage(ChatMessage chatMessage) {
    //     // 1️⃣ Save to DB

    //     Long sender_id=chatMessage.getSenderId();
    //     Long receiver_id=chatMessage.getReceiverId();
    //     String message=chatMessage.getContent();

    //     // 2️⃣ Save to DB
    //     chatService.saveMessage(sender_id, receiver_id, message);

    //     // 3️⃣ Send to receiver (private channel)
    //     messagingTemplate.convertAndSendToUser(
    //         String.valueOf(receiver_id),
    //         "/queue/messages",
    //         chatMessage
    //     );
    // }
    // @MessageMapping("/chat.sendMessage")
    // public void sendMessage(@Payload ChatMessage chatMessage) {
    //     // 1️⃣ Save to DB

    //     chatService.saveMessage(chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getContent());

    //     // 2️⃣ Send directly to the receiver’s topic
    //     messagingTemplate.convertAndSend(
    //         "/topic/messages/" + chatMessage.getReceiverId(),
    //         chatMessage
    //     );
    // }

    // Modify the sendMessage method in ChatController:
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Received payload: " + chatMessage); 

        Long savedChatId = null;

        if (chatMessage.getMedia() != null) {
            // Save message with media and get the chat ID
            savedChatId = chatService.saveMessageAndMedia(
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                chatMessage.getContent(),
                chatMessage.getRepliedToId(),
                chatMessage.getMedia()
            );
        } else {
            // Save regular message and get the chat ID
            savedChatId = chatService.saveMessage(
                chatMessage.getSenderId(), 
                chatMessage.getReceiverId(), 
                chatMessage.getContent(), 
                chatMessage.getRepliedToId()
            );
        }

        // 🔥 CREATE NOTIFICATION FOR THE RECEIVER
        if (savedChatId != null) {
            if (chatMessage.getRepliedToId() != null) {
                // It's a reply to a message
                notificationService.createReplyNotification(
                    chatMessage.getSenderId(), 
                    chatMessage.getReceiverId(), 
                    savedChatId, 
                    chatMessage.getRepliedToId()
                );
            } else {
                // It's a new message
                notificationService.createMessageNotification(
                    chatMessage.getSenderId(), 
                    chatMessage.getReceiverId(), 
                    savedChatId
                );
            }
        }

        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getReceiverId(), chatMessage);
    }


    @GetMapping("/session/currentUser")
    @ResponseBody
    public Map<String, Object> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return Map.of("userId", null);
        return Map.of("userId", user.getUser_id());
    }

}
