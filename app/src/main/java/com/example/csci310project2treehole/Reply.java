package com.example.csci310project2treehole;

public class Reply {
    private String replyId;
    private String authorId;
    private String authorName;
    private String content;
    private long timestamp;
    private boolean isAnonymous;

    public Reply() {
        // Default constructor required for calls to DataSnapshot.getValue(Reply.class)
    }

    public Reply(String replyId, String authorId, String authorName, String content, long timestamp, boolean isAnonymous) {
        this.replyId = replyId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.timestamp = timestamp;
        this.isAnonymous = isAnonymous;
    }

    // Getters and setters

    public String getReplyId() {
        return replyId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }
}
