package com.example.csci310project2treehole;

import java.util.Map;

public class User {
    private String name;
    private String email;
    private String uscid;
    private String role;
    private String profileImageUrl;
    private Map<String, Boolean> subscriptions;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
        this.profileImageUrl = "default_profile_image_url";
    }

    public User(String name, String email, String uscid, String role, String profileImageUrl, Map<String, Boolean> subscriptions) {
        this.name = name;
        this.email = email;
        this.uscid = uscid;
        this.role = role;
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "default_profile_image_url";
        this.subscriptions = subscriptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUscid() {
        return uscid;
    }

    public void setUscid(String uscid) {
        this.uscid = uscid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Map<String, Boolean> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map<String, Boolean> subscriptions) {
        this.subscriptions = subscriptions;
    }
}


