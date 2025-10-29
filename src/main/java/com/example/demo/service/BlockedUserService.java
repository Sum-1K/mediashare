package com.example.demo.service;

import com.example.demo.dao.BlockedUserDao;
import com.example.demo.model.BlockedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlockedUserService {

    @Autowired
    private BlockedUserDao blockedUserDao;

    public void blockUser(Long blockerId, Long blockedId) {
        BlockedUser blockedUser = new BlockedUser(blockerId, blockedId, LocalDateTime.now());
        blockedUserDao.save(blockedUser);
    }

    public void unblockUser(Long blockerId, Long blockedId) {
        blockedUserDao.deleteById(blockerId, blockedId);
    }

    public boolean isBlocked(Long blockerId, Long blockedId) {
        try {
            return blockedUserDao.findById(blockerId, blockedId) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public List<BlockedUser> getBlockedUsers(Long blockerId) {
        return blockedUserDao.findAllByBlocker(blockerId);
    }
    
}
