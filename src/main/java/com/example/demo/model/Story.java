package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stories")
public class Story {

    @Id
    private Long story_id;  // also foreign key to content_id

    @OneToOne
    @MapsId   // tells JPA that story_id = content_id
    @JoinColumn(name = "story_id")
    private Content content;

    private String media_file;

    private String highlight_topic;

    private boolean is_highlighted;

    private boolean is_archived;

    // Getters and Setters
    public Long getStory_id() { return story_id; }
    public void setStory_id(Long story_id) { this.story_id = story_id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public String getMedia_file() { return media_file; }
    public void setMedia_file(String media_file) { this.media_file = media_file; }

    public String getHighlight_topic() { return highlight_topic; }
    public void setHighlight_topic(String highlight_topic) { this.highlight_topic = highlight_topic; }

    public boolean isIs_highlighted() { return is_highlighted; }
    public void setIs_highlighted(boolean is_highlighted) { this.is_highlighted = is_highlighted; }

    public boolean isIs_archived() { return is_archived; }
    public void setIs_archived(boolean is_archived) { this.is_archived = is_archived; }
}
