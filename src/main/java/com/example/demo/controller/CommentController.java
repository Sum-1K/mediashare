package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.CommentDao;
import com.example.demo.model.Comment;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentDao commentDao;

    // ✅ Add comment
    @PostMapping("/add")
    public String addComment(@RequestParam("postId") Long postId,
                        @RequestParam("text") String text,
                        HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        Comment comment = new Comment();
        comment.setContent(text);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUserId(user.getUser_id());
        comment.setContentId(postId);

        commentDao.insert(comment);

        // Redirect back to post page
        return "redirect:/post/" + postId;
    }

    // // ✅ Delete comment
    // @PostMapping("/delete")
    // public String deleteComment(@RequestParam("commentId") Long commentId,
    //                             @RequestParam("postId") Long postId,
    //                             HttpSession session) {
    //     User user = (User) session.getAttribute("loggedInUser");
    //     if (user == null) {
    //         return "redirect:/users/login";
    //     }

    //     commentDao.deleteById(commentId);
    //     return "redirect:/post/" + postId;
    // }

    // ✅ Add comment for reel
    @PostMapping("/addReel")
    public String addReelComment(@RequestParam("reelId") Long reelId,
                            @RequestParam("text") String text,
                            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        Comment comment = new Comment();
        comment.setContent(text);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUserId(user.getUser_id());
        comment.setContentId(reelId);

        commentDao.insert(comment);
        System.out.println("[DEBUG] Reel comment added by userId=" + user.getUser_id() + " for reelId=" + reelId);

        // Redirect back to reel detail page
        return "redirect:/reels/" + reelId;
    }

    // ✅ Delete comment (works for both posts and reels)
    @PostMapping("/delete")
    public String deleteComment(@RequestParam("commentId") Long commentId,
                                @RequestParam("contentId") Long contentId,
                                @RequestParam("type") String type,
                                HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        commentDao.deleteById(commentId);
        System.out.println("[DEBUG] Comment deleted with id=" + commentId);

        // Redirect depending on type
        if ("reel".equalsIgnoreCase(type)) {
            return "redirect:/reels/" + contentId;
        } else {
            return "redirect:/post/" + contentId;
        }
    }

    // ✅ AJAX comment for Story
@PostMapping("/story/add")
@ResponseBody
public java.util.Map<String, Object> addStoryComment(
        @RequestParam Long contentId,
        @RequestParam String text,
        HttpSession session) {

    java.util.Map<String, Object> response = new java.util.HashMap<>();

    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        response.put("error", "User not logged in");
        return response;
    }

    Comment comment = new Comment();
    comment.setContent(text);
    comment.setCreatedAt(LocalDateTime.now());
    comment.setUserId(user.getUser_id());
    comment.setContentId(contentId);

    commentDao.insert(comment);
    System.out.println("[DEBUG] Story comment added by userId=" + user.getUser_id() + " for storyId=" + contentId);

    // ✅ Prepare response
    response.put("success", true);
    response.put("username", user.getUser_name());
    response.put("content", text);
    response.put("createdAt", comment.getCreatedAt().toString());

    return response;
}

    
}
