package com.msdevelopers.salepointapp.Model;

public class ModelOrderUser {

    private String OrderId,OrderTime,OrderStatus,OrderCost,OrderBy,OrderTo,latitude,longitude,deliveryFee;

    public ModelOrderUser() {
    }

    public ModelOrderUser(String orderId, String orderTime, String orderStatus,
                          String orderCost, String orderBy, String orderTo,
                          String latitude, String longitude, String deliveryFee) {
        OrderId = orderId;
        OrderTime = orderTime;
        OrderStatus = orderStatus;
        OrderCost = orderCost;
        OrderBy = orderBy;
        OrderTo = orderTo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryFee = deliveryFee;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderCost() {
        return OrderCost;
    }

    public void setOrderCost(String orderCost) {
        OrderCost = orderCost;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getOrderTo() {
        return OrderTo;
    }

    public void setOrderTo(String orderTo) {
        OrderTo = orderTo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
