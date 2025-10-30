package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.ChatDao;
import com.example.demo.dto.ChatMediaDTO;
import com.example.demo.dao.ChatMediaDao;
import com.example.demo.dto.ChatMessage;
import com.example.demo.model.Chat;
import com.example.demo.model.ChatMedia;
import com.example.demo.model.User;  // ✅ import User model
import java.util.List;  
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ChatService {
    @Autowired
    private ChatDao chatDao;
    private final ChatMediaDao chatMediaDao;

    public List<User> getChatUsers(Long currentUserId) {
        return chatDao.findChatUsers(currentUserId);
    }

    public List<ChatMessage> getMessagesBetween(Long currentUserId, Long otherUserId) {
    return chatDao.findMessagesBetweenWithMedia(currentUserId, otherUserId);
    }

    @Transactional
    public Long saveMessage(Long senderId, Long receiverId, String content, Long repliedToId) {
        Chat chat = new Chat();
        chat.setSender_id(senderId);
        chat.setReceiver_id(receiverId);
        chat.setMessage(content);
        chat.setReplied_to_id(repliedToId);

        Chat savedChat = chatDao.saveAndReturn(chat);
        
        // 🔥 RETURN THE CHAT ID FOR NOTIFICATIONS
        return savedChat.getChat_id();
    }

    @Transactional
    public Long saveMessageAndMedia(Long senderId, Long receiverId, String content, Long repliedToId, ChatMediaDTO mediaDTO) {
        // 1️⃣ Save chat message
        Chat chat = new Chat();
        chat.setSender_id(senderId);
        chat.setReceiver_id(receiverId);
        chat.setMessage(content);
        chat.setReplied_to_id(repliedToId);

        Chat savedChat=chatDao.saveAndReturn(chat);

        // 2️⃣ If there’s a media object, save it in ChatMedia
        if (mediaDTO != null && mediaDTO.getFileUrl() != null) {
            ChatMedia chatMedia = new ChatMedia();
            chatMedia.setChat_id(savedChat.getChat_id());
            chatMedia.setFile_type(mediaDTO.getFileType());
            chatMedia.setFile_url(mediaDTO.getFileUrl());
            chatMediaDao.save(chatMedia); 
        }

        return savedChat.getChat_id();
    }
}
