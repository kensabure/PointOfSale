package com.msdevelopers.salepointapp.Model;

public class ModelProducts {
    private String pid,date,time,title,image,price,
            priceDiscount,priceDiscountNote,
            discountAvailable,category,description,uid;

    public ModelProducts() {
    }

    public ModelProducts(String pid, String date, String time, String title,
                         String image, String price, String priceDiscount,
                         String priceDiscountNote, String discountAvailable,
                         String category, String description, String uid) {
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.title = title;
        this.image = image;
        this.price = price;
        this.priceDiscount = priceDiscount;
        this.priceDiscountNote = priceDiscountNote;
        this.discountAvailable = discountAvailable;
        this.category = category;
        this.description = description;
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(String priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public String getPriceDiscountNote() {
        return priceDiscountNote;
    }

    public void setPriceDiscountNote(String priceDiscountNote) {
        this.priceDiscountNote = priceDiscountNote;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
