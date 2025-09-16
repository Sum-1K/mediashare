package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "content_hashtag")
@IdClass(ContentHashtagId.class)
public class ContentHashtag {

    @Id
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Id
    @ManyToOne
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    // -------- Getters and Setters --------
    public Content getContent() { return content;}
    public void setContent(Content content) { this.content = content;}

    public Hashtag getHashtag() { return hashtag;}
    public void setHashtag(Hashtag hashtag) { this.hashtag = hashtag;}
}
