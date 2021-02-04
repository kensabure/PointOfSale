package com.msdevelopers.salepointapp.model;

public class Guest {
    private String UserName;
    private String Phone_No;
    private String Password;
    private String ImageUrl;


    public Guest() {
    }

    public Guest(String userName, String phone_No, String password, String imageUrl) {
        UserName = userName;
        Phone_No = phone_No;
        Password = password;
        ImageUrl = imageUrl;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhone_No() {
        return Phone_No;
    }

    public void setPhone_No(String phone_No) {
        Phone_No = phone_No;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}