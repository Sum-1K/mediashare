package com.example.demo.model;

public class Reel {
    private Long reelId;  // PK and also FK → content_id
    private String caption;
    private String videoFile;

    // Getters and setters
    public Long getReelId() {
        return reelId;
    }

    public void setReelId(Long reelId) {
        this.reelId = reelId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }
}
