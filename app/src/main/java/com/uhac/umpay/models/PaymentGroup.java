package com.uhac.umpay.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * same as reservationGroup, object for group of payment
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class PaymentGroup {

    @SerializedName("or_no")
    private String or_no;

    @SerializedName("reserve_code")
    private String reserveCode;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("product_orders")
    private ArrayList<ProductOrder> productOrders;

    @SerializedName("reserve_date")
    private Date reservedDate;

    @SerializedName("payment_date")
    private Date paymentDate;

    @SerializedName("date_today")
    private Date dateToday;

    public String getOr_no() {
        return or_no;
    }

    public void setOr_no(String or_no) {
        this.or_no = or_no;
    }

    public String getReserveCode() {
        return reserveCode;
    }

    public void setReserveCode(String reserveCode) {
        this.reserveCode = reserveCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ArrayList<ProductOrder> getProductOrders() {
        return productOrders;
    }

    public void setProductOrders(ArrayList<ProductOrder> productOrders) {
        this.productOrders = productOrders;
    }

    public Date getReservedDate() {
        return reservedDate;
    }

    public void setReservedDate(Date reservedDate) {
        this.reservedDate = reservedDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getDateToday() {
        return dateToday;
    }

    public void setDateToday(Date dateToday) {
        this.dateToday = dateToday;
    }
}
