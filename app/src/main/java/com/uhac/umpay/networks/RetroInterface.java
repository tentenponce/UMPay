package com.uhac.umpay.networks;

import com.uhac.umpay.models.Company;
import com.uhac.umpay.models.CustomerReservation;
import com.uhac.umpay.models.MsgResponse;
import com.uhac.umpay.models.PaymentGroup;
import com.uhac.umpay.models.Product;
import com.uhac.umpay.models.ReservationGroup;
import com.uhac.umpay.models.ReservationValidation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Holds all http requests of the app
 * Created by UHAC CALOOCAN on 12/10/2016.
 */
public interface RetroInterface {

    @GET("loadCompanies.php")
    Call<ArrayList<Company>> getCompanies();

    @GET("loadProducts.php")
    Call<ArrayList<Product>> getProducts(
            @Query("company_id") int cmpny_id
    );

    @POST("addReserve.php")
    Call<ArrayList<ReservationValidation>> addReserve(
            @Body CustomerReservation customerReservation);

    @GET("loadReservations.php")
    Call<ArrayList<ReservationGroup>> getReservations(
            @Query("bai_account_no") String accountNo
    );

    @GET("loadPayment.php")
    Call<ArrayList<PaymentGroup>> getPayments(
            @Query("bai_account_no") String accountNo
    );

    @GET("loadCOB.php")
    Call<MsgResponse> checkBalance(
            @Query("bai_account_no") String accountNo
    );

    @GET("validatePassword.php")
    Call<MsgResponse> validatePassword(
            @Query("bai_account_no") String accountNo,
            @Query("bai_password") String password
    );

    @GET("checkStatus.php")
    Call<MsgResponse> checkStatus(
            @Query("bai_account_no") String accountNo
    );

    @GET("editStatus.php")
    Call<MsgResponse> lockAccount(
            @Query("bai_account_no") String accountNo
    );
}
