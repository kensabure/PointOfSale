package com.msdevelopers.salepointapp.model;

public class OnboardingModel {
    //declare the variables
    int imageId;
    String title;
    String description;

    //empty constructor
    public OnboardingModel(){

    }

    //get and set

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
