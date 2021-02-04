package com.msdevelopers.salepointapp.model;

public class Items {
    String Name, Price, Image, time, date, pid;

    public Items() {
    }

    public Items(String name, String price, String image, String time, String date, String pid) {
        Name = name;
        Price = price;
        Image = image;
        this.time = time;
        this.date = date;
        this.pid = pid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
