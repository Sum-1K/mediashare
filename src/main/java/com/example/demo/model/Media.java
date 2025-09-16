package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long media_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Integer media_order;

    public enum MediaType {
        PHOTO, VIDEO
    }

    // Getters and Setters
    public Long getMedia_id() { return media_id; }
    public void setMedia_id(Long media_id) { this.media_id = media_id; }

    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public Integer getMedia_order() { return media_order; }
    public void setMedia_order(Integer media_order) { this.media_order = media_order; }
}
