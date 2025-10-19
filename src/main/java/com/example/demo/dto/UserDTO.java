package com.example.demo.dto;

public class UserDTO {
    private Long id;
    private String username;
    private boolean isCloseFriend;

    public UserDTO(Long id, String username, boolean isCloseFriend) {
        this.id = id;
        this.username = username;
        this.isCloseFriend = isCloseFriend;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public boolean isCloseFriend() { return isCloseFriend; }
    public void setCloseFriend(boolean closeFriend) { isCloseFriend = closeFriend; }
}
