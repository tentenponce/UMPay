package com.uhac.umpay.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Object for product
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class Product implements Parcelable{

    @SerializedName("prod_id")
    private int id;

    @SerializedName("prod_name")
    private String name;

    @SerializedName("pc_name")
    private String categoryName;

    @SerializedName("prod_desc")
    private String description;

    @SerializedName("prod_qty")
    private int qty;

    @SerializedName("prod_order_limit")
    private int orderLimit;

    @SerializedName("prod_price")
    private double price;

    @SerializedName("prod_code")
    private String code;

    @SerializedName("prod_img")
    private String img;

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        categoryName = in.readString();
        description = in.readString();
        qty = in.readInt();
        orderLimit = in.readInt();
        price = in.readDouble();
        code = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getOrderLimit() {
        return orderLimit;
    }

    public void setOrderLimit(int orderLimit) {
        this.orderLimit = orderLimit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(categoryName);
        parcel.writeString(description);
        parcel.writeInt(qty);
        parcel.writeInt(orderLimit);
        parcel.writeDouble(price);
        parcel.writeString(code);
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
