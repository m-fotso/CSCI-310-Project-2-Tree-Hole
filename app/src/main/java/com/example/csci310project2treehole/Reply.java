package com.example.csci310project2treehole;

public class Reply {
    private String replyId;
    private String authorId;
    private String authorName;
    private String content;
    private long timestamp;
    private boolean isAnonymous;
    private String anonymousName;
    private String parentReplyId;  // ID of the reply this is responding to
    private String replyToUsername;
    private boolean isExpanded;    // For UI state management

    public Reply() {
        // Default constructor required for Firebase
    }

    public Reply(String replyId, String authorId, String authorName, String content,
                 long timestamp, boolean isAnonymous, String parentReplyId, String replyToUsername) {
        this.replyId = replyId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.timestamp = timestamp;
        this.isAnonymous = isAnonymous;
        this.parentReplyId = parentReplyId;
        this.replyToUsername = replyToUsername;
        this.isExpanded = false;
    }

    public String getDisplayName() {
        return isAnonymous ? anonymousName : authorName;
    }

    // Getters and Setters
    public String getReplyId() { return replyId; }
    public void setReplyId(String replyId) { this.replyId = replyId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getAnonymousName() { return anonymousName; }
    public void setAnonymousName(String anonymousName) { this.anonymousName = anonymousName; }

    public String getParentReplyId() { return parentReplyId; }
    public void setParentReplyId(String parentReplyId) { this.parentReplyId = parentReplyId; }

    public String getReplyToUsername() { return replyToUsername; }
    public void setReplyToUsername(String replyToUsername) { this.replyToUsername = replyToUsername; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}