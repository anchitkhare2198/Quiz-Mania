package com.example.quizmania;

import java.util.List;
import java.util.Map;

public class ScoreModel {

    private String categoryName;
    private String setId;
    private String score,total;
    private String date;
    private String time;
    String key;

    public ScoreModel() {
    }

    public ScoreModel(String categoryName, String setId, String score, String total,String key, String date, String time) {
        this.categoryName = categoryName;
        this.setId = setId;
        this.score = score;
        this.total = total;
        this.key = key;
        this.date = date;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    //    private String score,total;
//
//    public ScoreModel() {
//    }
//
//    public ScoreModel(String score, String total) {
//        this.score = score;
//        this.total = total;
//    }
//
//    public String getScore() {
//        return score;
//    }
//
//    public void setScore(String score) {
//        this.score = score;
//    }
//
//    public String getTotal() {
//        return total;
//    }
//
//    public void setTotal(String total) {
//        this.total = total;
//    }
}
