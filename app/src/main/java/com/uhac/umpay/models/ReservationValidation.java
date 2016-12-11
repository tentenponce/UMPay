package com.uhac.umpay.models;

import com.google.gson.annotations.SerializedName;

/**
 * Reservation validation from php. (reserved or unreserved)
 * Created by Exequiel Egbert V. Ponce on 8/16/2016.
 */
public class ReservationValidation {

    public static final int UNPAID_RESERVE_RESPONSE = 0; //if already reserve
    public static final int AVAILABILITY_RESPONSE = 1; // if item available, server side validation
    public static final int RESERVED_RESPONSE = 3; //successful reserve

    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("reservation_group")
    private ReservationGroup reservationGroup;

    public ReservationGroup getReservationGroup() {
        return reservationGroup;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
