package com.example.quizmania;

public class UserProfile {

    private String fullname;
    private String username;
    private String Profile_url;

    public UserProfile() {

    }

    public UserProfile(String fullname, String username, String Profile_url) {
        this.fullname = fullname;
        this.username = username;
        this.Profile_url = Profile_url;
    }

    public String getProfile_url() {
        return Profile_url;
    }

    public void setProfile_url(String profile_url) {
        Profile_url = profile_url;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}


