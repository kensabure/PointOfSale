package com.msdevelopers.salepointapp.model;

public class ModelOrderUser {

    private String OrderId,OrderTime,OrderStatus,
            OrderCost,OrderBy,OrderTo,latitude,
            longitude,deliveryFee,Payment_Method,PaymentStatus,InVoice_No;

    public ModelOrderUser() {
    }

    public ModelOrderUser(String orderId, String orderTime, String orderStatus,
                          String orderCost, String orderBy, String orderTo,
                          String latitude, String longitude,
                          String deliveryFee, String payment_Method, String paymentStatus, String inVoice_No) {
        OrderId = orderId;
        OrderTime = orderTime;
        OrderStatus = orderStatus;
        OrderCost = orderCost;
        OrderBy = orderBy;
        OrderTo = orderTo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryFee = deliveryFee;
        Payment_Method = payment_Method;
        PaymentStatus = paymentStatus;
        InVoice_No = inVoice_No;
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

    public String getPayment_Method() {
        return Payment_Method;
    }

    public void setPayment_Method(String payment_Method) {
        Payment_Method = payment_Method;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getInVoice_No() {
        return InVoice_No;
    }

    public void setInVoice_No(String inVoice_No) {
        InVoice_No = inVoice_No;
    }
}
