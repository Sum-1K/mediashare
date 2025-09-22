package com.example.demo.model;

public class Media {

    private Long mediaId;
    private MediaType type;
    private String url;
    private Long postId;
    private Integer mediaOrder;

    public enum MediaType {
        PHOTO, VIDEO
    }

    public Media() {}

    public Media(Long mediaId, MediaType type, String url, Long postId, Integer mediaOrder) {
        this.mediaId = mediaId;
        this.type = type;
        this.url = url;
        this.postId = postId;
        this.mediaOrder = mediaOrder;
    }

    // Getters and setters
    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }

    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Integer getMediaOrder() { return mediaOrder; }
    public void setMediaOrder(Integer mediaOrder) { this.mediaOrder = mediaOrder; }
}