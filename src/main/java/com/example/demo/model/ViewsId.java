package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

// Composite primary key class
public class ViewsId implements Serializable {
    private Long user;
    private Long content;

    // default constructor
    public ViewsId() {}

    public ViewsId(Long user, Long content) {
        this.user = user;
        this.content = content;
    }

    // equals() and hashCode() required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewsId)) return false;
        ViewsId that = (ViewsId) o;
        return Objects.equals(user, that.user) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, content);
    }
}
