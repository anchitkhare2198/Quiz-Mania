package com.example.quizmania;

public class SubjectLeaderBoardModel {
    String finalScore,finalTotal,fullName;

    public SubjectLeaderBoardModel() {
    }

    public SubjectLeaderBoardModel(String finalScore, String finalTotal, String fullName) {
        this.finalScore = finalScore;
        this.finalTotal = finalTotal;
        this.fullName = fullName;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(String finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
