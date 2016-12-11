package com.uhac.umpay.models;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;

/**
 *
 * Created by Exequiel Egbert V. Ponce on 7/29/2016.
 */
public class ProductCart implements Parcelable {

    private ArrayList<ProductOrder> products = new ArrayList<>();
    private String errorMsg;

    public ProductCart() {}

    private ProductCart(Parcel in) {
        in.readTypedList(products, ProductOrder.CREATOR);
        errorMsg = in.readString();
    }

    public boolean addItem(ProductOrder productOrder) {
        boolean isExist = false;
        int staticSize = getCartSize(); // to avoid looping forever
        for (int i = 0; i < staticSize; i ++) {
            if (getProductItem(i).getProductItem().getId() == productOrder.getProductItem().getId()) { //check if the item already exist in the list
                products.remove(i); //remove it

                if (productOrder.getQuantity() != 0) {
                    products.add(productOrder); //then add the updated quantity
                }

                isExist = true;
            }
        }

        if (!isExist && productOrder.getQuantity() != 0) {
            products.add(productOrder); //then add the updated quantity
        }

        return true;
    }

    public void removeItem(int index) {
        products.remove(index);
    }

    public ProductOrder getProductItem(int index) {
        return products.get(index);
    }

    //search proware cart by id
    public ProductOrder getProwareItemById(int id) {
        int staticSize = getCartSize();
        for (int i = 0; i < staticSize; i ++) {
            if (getProductItem(i).getProductItem().getId() == id) {
                return getProductItem(i);
            }
        }

        return null;
    }

    public boolean isExists(Product product) {
        for (int i = 0; i < products.size(); i ++) {
            if (products.get(i).getProductItem().getId() == product.getId()) {
                return true;
            }
        }

        return false;
    }

    public int getCartSize() {
        return products.size();
    }

    public boolean isEmpty() {
        return getCartSize() == 0;
    }

    public double getTotalCartPrice() {
        double totalPrice = 0;
        int cartSize = getCartSize();
        for (int i = 0; i < cartSize; i ++) {
            int qty = getProductItem(i).getQuantity();
            double price = getProductItem(i).getProductItem().getPrice();

            totalPrice += price * qty;
        }

        return totalPrice;
    }

    public ArrayList<ProductOrder> getProductOrders() {
        return products;
    }

    //remove all items on the cart
    public void emptyCart() {
        products.clear();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(products);
        parcel.writeString(errorMsg);
    }

    public static final Creator<ProductCart> CREATOR = new Creator<ProductCart>() {
        public ProductCart createFromParcel(Parcel in) {
            return new ProductCart(in);
        }

        public ProductCart[] newArray(int size) {
            return new ProductCart[size];
        }
    };
}
