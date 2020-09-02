package com.example.quizmania;

public class PreviousScoreModel {

    private String categoryName,score,total,setId,date,time;

    public PreviousScoreModel() {
    }

    public PreviousScoreModel(String categoryName, String score, String total, String setId, String date, String time) {
        this.categoryName = categoryName;
        this.score = score;
        this.total = total;
        this.setId = setId;
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

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
}
