package com.uhac.umpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uhac.umpay.R;
import com.uhac.umpay.adapters.PaymentAdapter;
import com.uhac.umpay.models.PaymentGroup;
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

public class MyTransactionActivity extends AppCompatActivity {

    private ArrayList<PaymentGroup> paymentGroups = new ArrayList<>();

    private String accountNo = "";

    PaymentAdapter paymentAdapter;

    private AppCompatTextView msgReservationTV;

    CoordinatorLayout myReserveCoor;
    SwipeRefreshLayout myReserveSwipe;
    RecyclerView myReserveRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        setTitle("Transaction History");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra("bai_account_no")) {
            accountNo = getIntent().getStringExtra("bai_account_no");
        }

        myReserveCoor = (CoordinatorLayout) findViewById(R.id.myReserveCoor);
        myReserveSwipe = (SwipeRefreshLayout) findViewById(R.id.myReserveSwipe);
        myReserveRecyclerView = (RecyclerView) findViewById(R.id.myReserveRecyclerView);
        msgReservationTV = (AppCompatTextView) findViewById(R.id.msgReservationTV);
        msgReservationTV = (AppCompatTextView) findViewById(R.id.msgReservationTV);

        msgReservationTV.setVisibility(View.GONE);

        paymentAdapter = new PaymentAdapter(MyTransactionActivity.this, paymentGroups);

        myReserveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myReserveRecyclerView.setNestedScrollingEnabled(false); //smooth scrolling
        myReserveRecyclerView.setAdapter(paymentAdapter);

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
            IntentIntegrator scanIntegrator = new IntentIntegrator(MyTransactionActivity.this);
            scanIntegrator.initiateScan();

            myReserveSwipe.post(new Runnable() {
                @Override
                public void run() {
                    myReserveSwipe.setRefreshing(false);
                }
            });
        } else {
            RetroInterface retroInterface = RetroClient.getClient(MyTransactionActivity.this).create(RetroInterface.class);
            Call<ArrayList<PaymentGroup>> call = retroInterface.getPayments(accountNo);

            call.enqueue(new Callback<ArrayList<PaymentGroup>>() {
                @Override
                public void onResponse(Call<ArrayList<PaymentGroup>> call, Response<ArrayList<PaymentGroup>> response) {
                    if (response.body() != null) {
                        if (response.body().size() <= 0) {
                            Toast.makeText(MyTransactionActivity.this, "You don't have any past transactions.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        paymentGroups.clear();
                        paymentGroups.addAll(response.body());

                        paymentAdapter.notifyDataSetChanged();
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
                public void onFailure(Call<ArrayList<PaymentGroup>> call, Throwable t) {
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
