package com.example.myapplication;

public class ProgressModel {

    private String name;
    private int progress;
    private String value;


    public ProgressModel(String name, int progress) {
        this.name = name;
        this.progress = progress;
//        this.value = progress.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
