package com.example.demo.dto;

public class UserDTO {
    private Long id;
    private String username;
    private boolean isCloseFriend;
    private boolean isBlocked;
    private String photo; // ADD THIS

    public UserDTO(Long id, String username, boolean isCloseFriend, boolean isBlocked, String photo) {
        this.id = id;
        this.username = username;
        this.isCloseFriend = isCloseFriend;
        this.isBlocked = isBlocked;
        this.photo = photo;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public boolean isCloseFriend() { return isCloseFriend; }
    public void setCloseFriend(boolean closeFriend) { isCloseFriend = closeFriend; }
    public boolean isBlocked() { 
        return isBlocked; 
    }

    public void setBlocked(boolean blocked) { 
        this.isBlocked = blocked; 
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

