package com.example.csci310project2treehole;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postId;
    private String authorId;
    private String authorName;
    private String title;
    private String content;
    private long timestamp;
    private boolean isAnonymous;
    private Map<String, Reply> replies;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String postId, String authorId, String authorName, String title, String content, long timestamp, boolean isAnonymous) {
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.isAnonymous = isAnonymous;
        this.replies = new HashMap<>();
    }

    // Getter and Setter for postId
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter and Setter for authorId
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    // Getter and Setter for authorName
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    // Getter and Setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and Setter for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter and Setter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getter and Setter for isAnonymous
    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    // Getter and Setter for replies
    public Map<String, Reply> getReplies() {
        return replies;
    }

    public void setReplies(Map<String, Reply> replies) {
        this.replies = replies;
    }
}

