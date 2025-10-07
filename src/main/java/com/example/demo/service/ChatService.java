package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ChatDao;
import com.example.demo.model.Chat;
import com.example.demo.model.User;  // ✅ import User model
import java.util.List;  


@Service
public class ChatService {
    @Autowired
    private ChatDao chatDao;

    public List<User> getChatUsers(Long currentUserId) {
        return chatDao.findChatUsers(currentUserId);
    }

    public List<Chat> getMessagesBetween(Long currentUserId, Long otherUserId) {
    return chatDao.findMessagesBetween(currentUserId, otherUserId);
    }

    public void saveMessage(Long senderId, Long recieverId, String text){
        chatDao.saveMessage(senderId, recieverId, text);
    }
}
