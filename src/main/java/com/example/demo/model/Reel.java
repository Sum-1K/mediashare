package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reels")
public class Reel {

    @Id
    private Long reel_id;  // also foreign key to content_id

    @OneToOne
    @MapsId   // reel_id = content_id
    @JoinColumn(name = "reel_id")
    private Content content;

    @Column(length = 500) // optional limit on caption
    private String caption;

    private String video_file;

    // Getters and Setters
    public Long getReel_id() { return reel_id; }
    public void setReel_id(Long reel_id) { this.reel_id = reel_id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getVideo_file() { return video_file; }
    public void setVideo_file(String video_file) { this.video_file = video_file; }
}
