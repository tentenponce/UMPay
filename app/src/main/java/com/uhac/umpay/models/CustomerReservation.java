package com.uhac.umpay.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * to be throw on retrofit, reservation to add on database
 * Created by Exequiel Egbert V. Ponce on 8/17/2016.
 */
public class CustomerReservation {

    @SerializedName("bai_account_no")
    private String accountNo;

    @SerializedName("product_orders")
    private ArrayList<ProductOrder> productOrders;

    @SerializedName("deliver_id")
    private int deliverId;

    @SerializedName("deliver_address")
    private String deliverAddress;

    public CustomerReservation(String accountNo, ArrayList<ProductOrder> productOrders, int deliverId, String deliverAddress) {
        this.accountNo = accountNo;
        this.productOrders = productOrders;
        this.deliverId = deliverId;
        this.deliverAddress = deliverAddress;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public ArrayList<ProductOrder> getProductOrders() {
        return productOrders;
    }

    public void setProductOrders(ArrayList<ProductOrder> productOrders) {
        this.productOrders = productOrders;
    }

    public int getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(int deliverId) {
        this.deliverId = deliverId;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }
}
