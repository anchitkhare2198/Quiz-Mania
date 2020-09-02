package com.example.quizmania;

import java.util.List;

public class DirectoryModel {

    private String Dir_Name;
    private List<String> sets;
    private String key;

    public DirectoryModel() {
    }

    public DirectoryModel(String dir_Name, List<String> sets, String key) {
        Dir_Name = dir_Name;
        this.sets = sets;
        this.key = key;
    }

    public String getDir_Name() {
        return Dir_Name;
    }

    public void setDir_Name(String dir_Name) {
        Dir_Name = dir_Name;
    }

    public List<String> getSets() {
        return sets;
    }

    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
