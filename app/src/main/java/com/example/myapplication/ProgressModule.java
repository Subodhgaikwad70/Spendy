package com.example.myapplication;

public class ProgressModule {

    String category;
    int progress;
    String color;
    String type;

    public ProgressModule(String category, int progress, String color, String type) {
        this.category = category;
        this.progress = progress;
        this.color = color;
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
