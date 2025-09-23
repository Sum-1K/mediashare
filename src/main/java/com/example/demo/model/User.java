package com.example.demo.model;

// import jakarta.persistence.*;
// import org.hibernate.annotations.CreationTimestamp;

// import java.time.LocalDate;
// import java.time.LocalDateTime;

// @Entity
// @Table(name="users")   ///THIS LINE for supabase
// public class User {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long user_id;

//     @Column(nullable = false, unique = true)
//     private String user_name;

//     private String first_name;
//     private String last_name;

//     private LocalDate dob;

//     @Column(nullable = false, unique = true)
//     private String email;

//     @Lob
//     private String bio;

//     @Enumerated(EnumType.STRING)
//     private Privacy privacy;

//     private String photo;  // store file URL or path

//     @CreationTimestamp
//     private LocalDateTime join_date;

//     public enum Privacy {
//         PRIVATE, PUBLIC
//     }

//     // Getters and Setters
//     public Long getUser_id() { return user_id; }
//     public void setUser_id(Long user_id) { this.user_id = user_id; }

//     public String getUser_name() { return user_name; }
//     public void setUser_name(String user_name) { this.user_name = user_name; }

//     public String getFirst_name() { return first_name; }
//     public void setFirst_name(String first_name) { this.first_name = first_name; }

//     public String getLast_name() { return last_name; }
//     public void setLast_name(String last_name) { this.last_name = last_name; }

//     public LocalDate getDob() { return dob; }
//     public void setDob(LocalDate dob) { this.dob = dob; }

//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }

//     public String getBio() { return bio; }
//     public void setBio(String bio) { this.bio = bio; }

//     public Privacy getPrivacy() { return privacy; }
//     public void setPrivacy(Privacy privacy) { this.privacy = privacy; }

//     public String getPhoto() { return photo; }
//     public void setPhoto(String photo) { this.photo = photo; }

//     public LocalDateTime getJoin_date() { return join_date; }
//     public void setJoin_date(LocalDateTime join_date) { this.join_date = join_date; }
// }

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {

    public enum Privacy { PRIVATE, PUBLIC }

    private Long user_id;
    private String user_name;
    private String first_name;
    private String last_name;
    private LocalDate dob;
    private String email;
    private String bio;
    private Privacy privacy;
    private String photo;
    private LocalDateTime join_date;
    private String password;

    // Getters & setters
    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Privacy getPrivacy() { return privacy; }
    public void setPrivacy(Privacy privacy) { this.privacy = privacy; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public LocalDateTime getJoin_date() { return join_date; }
    public void setJoin_date(LocalDateTime join_date) { this.join_date = join_date; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}