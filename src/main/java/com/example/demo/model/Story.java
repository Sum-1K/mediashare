package com.example.demo.model;

public class Story {
    private Long storyId;  // PK and also FK → content_id
    private String mediaFile;
    private String highlightTopic;
    private Boolean isHighlighted;
    private Boolean isArchived;

    // Getters and setters
    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public String getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(String mediaFile) {
        this.mediaFile = mediaFile;
    }

    public String getHighlightTopic() {
        return highlightTopic;
    }

    public void setHighlightTopic(String highlightTopic) {
        this.highlightTopic = highlightTopic;
    }

    public Boolean getIsHighlighted() {
        return isHighlighted;
    }

    public void setIsHighlighted(Boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
}
