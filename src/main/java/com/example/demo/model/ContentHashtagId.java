package com.example.demo.model;

import java.io.Serializable;
import java.util.Objects;

public class ContentHashtagId implements Serializable {
    private Long content;
    private Long hashtag;

    // Default constructor
    public ContentHashtagId() {}

    public ContentHashtagId(Long content, Long hashtag) {
        this.content = content;
        this.hashtag = hashtag;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentHashtagId)) return false;
        ContentHashtagId that = (ContentHashtagId) o;
        return Objects.equals(content, that.content) &&
            Objects.equals(hashtag, that.hashtag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, hashtag);
    }
}
