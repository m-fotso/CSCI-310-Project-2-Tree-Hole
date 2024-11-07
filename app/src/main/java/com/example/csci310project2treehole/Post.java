package com.example.csci310project2treehole;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postId;
    private String authorId;
    private String authorName;
    private String authorProfileImageUrl;
    private String title;
    private String content;
    private long timestamp;
    private boolean isAnonymous;
    private Map<String, Reply> replies;
    private String category;
    private Map<String, String> anonymousUsers; // Maps userId to their anonymous name
    private Map<String, Boolean> userAnonymousStatus; // Tracks if each user is anonymous in this thread
    private int nextAnonymousNumber;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        this.replies = new HashMap<>();
        this.anonymousUsers = new HashMap<>();
        this.userAnonymousStatus = new HashMap<>();
        this.nextAnonymousNumber = 1;
    }

    public Post(String postId, String authorId, String authorName, String title,
                String content, long timestamp, boolean isAnonymous) {
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.isAnonymous = isAnonymous;
        this.replies = new HashMap<>();
        this.anonymousUsers = new HashMap<>();
        this.userAnonymousStatus = new HashMap<>();
        this.nextAnonymousNumber = 1;

        // Set the author's anonymous status and name if anonymous
        if (isAnonymous) {
            userAnonymousStatus.put(authorId, true);
            anonymousUsers.put(authorId, "Anonymous 1");
            nextAnonymousNumber = 2;
        } else {
            userAnonymousStatus.put(authorId, false);
        }
    }

    // Method to get or create anonymous name for a user
    public String getAnonymousNameForUser(String userId) {
        if (!anonymousUsers.containsKey(userId)) {
            String anonymousName = "Anonymous " + nextAnonymousNumber;
            anonymousUsers.put(userId, anonymousName);
            nextAnonymousNumber++;
            return anonymousName;
        }
        return anonymousUsers.get(userId);
    }

    // Method to check if a user should be anonymous in this thread
    public boolean shouldUserBeAnonymous(String userId) {
        // If user has already participated, return their stored status
        if (userAnonymousStatus.containsKey(userId)) {
            return userAnonymousStatus.get(userId);
        }
        // If new user, return false (they haven't chosen anonymity yet)
        return false;
    }

    // Method to set a user's anonymous status for this thread
    public void setUserAnonymousStatus(String userId, boolean isAnonymous) {
        userAnonymousStatus.put(userId, isAnonymous);
        if (isAnonymous && !anonymousUsers.containsKey(userId)) {
            // If becoming anonymous for the first time, assign a name
            getAnonymousNameForUser(userId);
        }
    }

    // Method to get display name for a user (either real or anonymous)
    public String getDisplayNameForUser(String userId, String defaultName) {
        Boolean isUserAnonymous = userAnonymousStatus.get(userId);
        if (isUserAnonymous != null && isUserAnonymous) {
            return anonymousUsers.get(userId);
        }
        return defaultName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return isAnonymous ? anonymousUsers.get(authorId) : authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfileImageUrl() {
        return authorProfileImageUrl;
    }

    public void setAuthorProfileImageUrl(String authorProfileImageUrl) {
        this.authorProfileImageUrl = authorProfileImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public Map<String, Reply> getReplies() {
        return replies;
    }

    public void setReplies(Map<String, Reply> replies) {
        this.replies = replies;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getAnonymousUsers() {
        return anonymousUsers;
    }

    public void setAnonymousUsers(Map<String, String> anonymousUsers) {
        this.anonymousUsers = anonymousUsers;
    }

    public Map<String, Boolean> getUserAnonymousStatus() {
        return userAnonymousStatus;
    }

    public void setUserAnonymousStatus(Map<String, Boolean> userAnonymousStatus) {
        this.userAnonymousStatus = userAnonymousStatus;
    }

    public int getNextAnonymousNumber() {
        return nextAnonymousNumber;
    }

    public void setNextAnonymousNumber(int nextAnonymousNumber) {
        this.nextAnonymousNumber = nextAnonymousNumber;
    }
}