package com.uhac.umpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uhac.umpay.R;
import com.uhac.umpay.adapters.ReservationAdapter;
import com.uhac.umpay.models.ReservationGroup;
import com.uhac.umpay.networks.RetroClient;
import com.uhac.umpay.networks.RetroInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * the customer's reservation list
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class MyReservationActivity extends AppCompatActivity {

    private ArrayList<ReservationGroup> reservationGroups = new ArrayList<>();

    private boolean resetCart = false;

    private String accountNo = "";

    ReservationAdapter reservationAdapter;

    CoordinatorLayout myReserveCoor;
    SwipeRefreshLayout myReserveSwipe;
    RecyclerView myReserveRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        setTitle("My Reservations");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra("bai_account_no")) {
            accountNo = getIntent().getStringExtra("bai_account_no");
        }

        resetCart = getIntent().getBooleanExtra("reset_cart", false);

        myReserveCoor = (CoordinatorLayout) findViewById(R.id.myReserveCoor);
        myReserveSwipe = (SwipeRefreshLayout) findViewById(R.id.myReserveSwipe);
        myReserveRecyclerView = (RecyclerView) findViewById(R.id.myReserveRecyclerView);

        reservationAdapter = new ReservationAdapter(MyReservationActivity.this, reservationGroups);

        myReserveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myReserveRecyclerView.setNestedScrollingEnabled(false); //smooth scrolling
        myReserveRecyclerView.setAdapter(reservationAdapter);

        myReserveSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReservations(accountNo);
            }
        });

        loadReservations(accountNo);
    }

    private void loadReservations(String accountNo) {
        myReserveSwipe.post(new Runnable() {
            @Override
            public void run() {
                myReserveSwipe.setRefreshing(true);
            }
        });

        if (accountNo.trim().equals("")) {
            Toast.makeText(this, "Scan your card.", Toast.LENGTH_LONG).show();
            IntentIntegrator scanIntegrator = new IntentIntegrator(MyReservationActivity.this);
            scanIntegrator.initiateScan();

            myReserveSwipe.post(new Runnable() {
                @Override
                public void run() {
                    myReserveSwipe.setRefreshing(false);
                }
            });
        } else {
            RetroInterface retroInterface = RetroClient.getClient(MyReservationActivity.this).create(RetroInterface.class);
            Call<ArrayList<ReservationGroup>> call = retroInterface.getReservations(accountNo);

            call.enqueue(new Callback<ArrayList<ReservationGroup>>() {
                @Override
                public void onResponse(Call<ArrayList<ReservationGroup>> call, Response<ArrayList<ReservationGroup>> response) {
                    if (response.body() != null) {

                        if (response.body().size() <= 0) {
                            Toast.makeText(MyReservationActivity.this, "You don't have any reservations.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        reservationGroups.clear();
                        reservationGroups.addAll(response.body());

                        reservationAdapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(myReserveCoor, "There's something wrong.", Snackbar.LENGTH_SHORT).show();
                    }

                    myReserveSwipe.post(new Runnable() {
                        @Override
                        public void run() {
                            myReserveSwipe.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ArrayList<ReservationGroup>> call, Throwable t) {
                    Snackbar.make(myReserveCoor, "There's something wrong.", Snackbar.LENGTH_SHORT).show();
                    myReserveSwipe.post(new Runnable() {
                        @Override
                        public void run() {
                            myReserveSwipe.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningIntentResult != null) { //if result is not null
            String scanContent = scanningIntentResult.getContents(); //account number
            String scanFormat = scanningIntentResult.getFormatName();

            if(scanContent != null && scanFormat != null) {
                accountNo = scanContent.trim();
                loadReservations(accountNo);
            } else {
                onBackPressed();
            }
        } else {
            onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
