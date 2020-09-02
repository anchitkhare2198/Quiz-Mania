package com.example.quizmania;

public class Member {

    private String VideoName;
    private String VideoUrl;

    public Member() {
    }

    public Member(String videoName, String videoUrl) {
        VideoName = videoName;
        VideoUrl = videoUrl;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }
}
