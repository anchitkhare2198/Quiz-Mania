package com.example.quizmania;

public class LeaderModel {
    String fullName,grandTotal,totalScore;

    public LeaderModel() {
    }

    public LeaderModel(String fullName, String grandTotal, String totalScore) {
        this.fullName = fullName;
        this.grandTotal = grandTotal;
        this.totalScore = totalScore;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }
}
