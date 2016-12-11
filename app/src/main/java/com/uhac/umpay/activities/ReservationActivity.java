package com.uhac.umpay.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uhac.umpay.R;
import com.uhac.umpay.adapters.ReserveAdapter;
import com.uhac.umpay.models.CustomerReservation;
import com.uhac.umpay.models.MsgResponse;
import com.uhac.umpay.models.ProductCart;
import com.uhac.umpay.models.ProductOrder;
import com.uhac.umpay.models.ReservationValidation;
import com.uhac.umpay.networks.RetroClient;
import com.uhac.umpay.networks.RetroInterface;
import com.uhac.umpay.utilities.Caloocan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Confirm Reservations
 * Created by Exequiel Egbert V. Ponce on 7/29/2016.
 */
public class ReservationActivity extends AppCompatActivity {

    private int deliverId = 0;
    private String deliverAddress = "";

    ProductCart productCart;

    CoordinatorLayout confirmCoor;
    RecyclerView recyclerView;
    TextView totalPriceTV;
    FloatingActionButton reserveFab;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        confirmCoor = (CoordinatorLayout) findViewById(R.id.confirmCoor);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        totalPriceTV = (TextView) findViewById(R.id.totalPriceTV);
        reserveFab = (FloatingActionButton) findViewById(R.id.reserveFab);

        productCart = getIntent().getParcelableExtra("productCart");

        //recycle view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReservationActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ReserveAdapter(productCart));

        //total price
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        totalPriceTV.setText("Total: " + Caloocan.PESO_SIGN + " " + numberFormat.format(productCart.getTotalCartPrice()));

        //progress dialog
        progressDialog = new ProgressDialog(ReservationActivity.this);
        progressDialog.setMessage("Reserving...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        //reservation
        reserveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
                builder.setTitle("Reservation Type");
                final LinearLayout linearLayout = new LinearLayout(ReservationActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                builder.setView(linearLayout);

                final AppCompatDialog appCompatDialog = builder.create();

                for (int i = 0; i < 2; i++) {
                    LinearLayout listItemLin = (LinearLayout) getLayoutInflater().inflate(R.layout.list_selectable_item_radio, linearLayout, false);
                    AppCompatTextView listItemTV = (AppCompatTextView) listItemLin.findViewById(R.id.listItemTV);
                    AppCompatRadioButton listItemRadio = (AppCompatRadioButton) listItemLin.findViewById(R.id.listItemRadio);
                    listItemRadio.setVisibility(View.GONE);

                    if (i == 0) {
                        listItemTV.setText("Pick up");
                    } else if (i == 1) {
                        listItemTV.setText("Cash on Delivery");
                    }

                    final int finalI = i;
                    View.OnClickListener onClickAnnouncementTypeListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (finalI == 0) { //if pick up
                                deliverId = 0;
                                IntentIntegrator scanIntegrator = new IntentIntegrator(ReservationActivity.this);
                                scanIntegrator.initiateScan();
                            } else { //if delivery
                                deliverId = 1;

                                final AppCompatEditText addressEDT = new AppCompatEditText(ReservationActivity.this);

                                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
                                builder
                                        .setTitle("Enter Address")
                                        .setMessage("Address where the item will be delivered.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deliverAddress = addressEDT.getText().toString().trim();
                                                IntentIntegrator scanIntegrator = new IntentIntegrator(ReservationActivity.this);
                                                scanIntegrator.initiateScan();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                                builder.setView(addressEDT, 50, 0, 50, 0);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                                if (negativeButton != null) {
                                    negativeButton.setBackgroundColor(Color.WHITE);
                                    negativeButton.setTextColor(Color.BLACK);
                                }

                                if (positiveButton != null) {
                                    positiveButton.setBackgroundColor(Color.WHITE);
                                    positiveButton.setTextColor(Color.BLACK);
                                }
                            }

                            if (appCompatDialog.isShowing()) {
                                appCompatDialog.dismiss();
                            }
                        }
                    };
                    listItemLin.setOnClickListener(onClickAnnouncementTypeListener);

                    linearLayout.addView(listItemLin);
                }

                appCompatDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningIntentResult != null) { //if result is not null

            String scanContent = scanningIntentResult.getContents();
            String scanFormat = scanningIntentResult.getFormatName();

            if(scanContent != null && scanFormat != null) {
                final String accountNo = scanContent;

                //validate PIN
                final AppCompatEditText input = new AppCompatEditText(ReservationActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);

                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
                builder
                        .setTitle("Enter Password")
                        .setMessage("Please enter your password for verification.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RetroInterface retroInterface = RetroClient.getClient(ReservationActivity.this).create(RetroInterface.class);
                                Call<MsgResponse> call = retroInterface.validatePassword(accountNo, input.getText().toString().trim());

                                call.enqueue(new Callback<MsgResponse>() {
                                    @Override
                                    public void onResponse(Call<MsgResponse> call, Response<MsgResponse> response) {
                                        if (response.body() != null) {
                                            if (response.body().getResponse_code() == 1) { //success
                                                Snackbar.make(confirmCoor, response.body().getResponse_msg(), Snackbar.LENGTH_LONG).show();

                                                //reserve item
                                                progressDialog.show();

                                                CustomerReservation customerReservation = new CustomerReservation(accountNo, productCart.getProductOrders(), deliverId, deliverAddress);
                                                RetroInterface retroInterface = RetroClient.getClient(ReservationActivity.this).create(RetroInterface.class);
                                                Call<ArrayList<ReservationValidation>> call2 = retroInterface.addReserve(customerReservation);
                                                call2.enqueue(new Callback<ArrayList<ReservationValidation>>() {
                                                    @Override
                                                    public void onResponse(Call<ArrayList<ReservationValidation>> call2, retrofit2.Response<ArrayList<ReservationValidation>> response) {
                                                        ArrayList<ReservationValidation> reservationValidations = response.body();

                                                        ArrayList<ProductOrder> unReserveOrders = new ArrayList<>();
                                                        ArrayList<String> validationMsgs = new ArrayList<>();

                                                        for (int i = 0; i < reservationValidations.size(); i++) {
                                                            ArrayList<ProductOrder> productOrders = reservationValidations.get(i).getReservationGroup().getProductOrders();

                                                            //condition for unpaid reservations, or already reserve
                                                            if (reservationValidations.get(i).getResponseCode() == ReservationValidation.UNPAID_RESERVE_RESPONSE) {

                                                                String not_reserve_items = "";
                                                                for (int j = 0; j < productOrders.size(); j++) {
                                                                    if (not_reserve_items.equals("")) {
                                                                        not_reserve_items = productOrders.get(j).getProductItem().getName();
                                                                    } else {
                                                                        not_reserve_items += ", " + productOrders.get(j).getProductItem().getName();
                                                                    }
                                                                }

                                                                validationMsgs.add("You have already reserve for " +
                                                                        not_reserve_items + ", please pay for it first before reserving again.");

                                                                //condition if the product is really available
                                                            } else if (reservationValidations.get(i).getResponseCode() == ReservationValidation.AVAILABILITY_RESPONSE) {

                                                                String not_reserve_items = "";
                                                                String linking_verb = "";
                                                                for (int j = 0; j < productOrders.size(); j++) {
                                                                    if (not_reserve_items.equals("")) {
                                                                        not_reserve_items = productOrders.get(j).getProductItem().getName();
                                                                        linking_verb = "is";
                                                                    } else {
                                                                        not_reserve_items += ", " + productOrders.get(j).getProductItem().getName();
                                                                        linking_verb = "are";
                                                                    }
                                                                }

                                                                validationMsgs.add("Sorry, " + not_reserve_items + " " + linking_verb +
                                                                        " out of stock. Someone had already reserved for it as of now.");

                                                                //syccessful reservation
                                                            } else if (reservationValidations.get(i).getResponseCode() == ReservationValidation.RESERVED_RESPONSE) {
                                                                String reserved_items = "";

                                                                for (int j = 0; j < productOrders.size(); j++) {
                                                                    if (reserved_items.equals("")) {
                                                                        reserved_items = productOrders.get(j).getProductItem().getName();
                                                                    } else {
                                                                        reserved_items += ", " + productOrders.get(j).getProductItem().getName();
                                                                    }
                                                                }

                                                                Intent intent = new Intent(ReservationActivity.this, MyReservationActivity.class);
                                                                intent.putExtra("bai_account_no", accountNo);
                                                                intent.putExtra("reset_cart", true);

                                                                startActivity(intent);

                                                                finish();
                                                            }

                                                            for (int j = 0; j < productOrders.size(); j++) {
                                                                boolean isIncluded = false;
                                                                for (int k = 0; k < unReserveOrders.size(); k++) {
                                                                    if (productOrders.get(j).getProductItem().getId() == unReserveOrders.get(k).getProductItem().getId()) {
                                                                        isIncluded = true;
                                                                    }
                                                                }

                                                                if (!isIncluded) {
                                                                    unReserveOrders.add(productOrders.get(j));
                                                                }
                                                            }
                                                        }

                                                        //build the snackbar validation message
                                                        String snackBarMsg = "";
                                                        for (int j = 0; j < validationMsgs.size(); j++) {
                                                            snackBarMsg += validationMsgs.get(j);

                                                            if (j != validationMsgs.size() - 1) { //if there's more, add new line
                                                                snackBarMsg += "\n";
                                                            }
                                                        }

                                                        Snackbar snackbar = Snackbar.make(confirmCoor, snackBarMsg, Snackbar.LENGTH_INDEFINITE);
                                                        snackbar.setAction("My Reservations", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                startActivity(new Intent(ReservationActivity.this, MyReservationActivity.class));
                                                                finish();
                                                            }
                                                        });

                                                        View snackbarView = snackbar.getView(); //get your snackbar view
                                                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text); //Get reference of snackbar textview
                                                        textView.setMaxLines(3); // Change your max lines

                                                        snackbar.show();

                                                        if (progressDialog.isShowing()) {
                                                            progressDialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ArrayList<ReservationValidation>> call2, Throwable t) {
                                                        Snackbar.make(confirmCoor, "Please try again later.", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else if (response.body().getResponse_code() == -1) { //invalid
                                                Snackbar.make(confirmCoor, response.body().getResponse_msg(), Snackbar.LENGTH_LONG).show();
                                            } else if (response.body().getResponse_code() == -2) {
                                                Snackbar.make(confirmCoor, response.body().getResponse_msg(), Snackbar.LENGTH_LONG).show();
                                            } else { //wtfzxc error
                                                Snackbar.make(confirmCoor, "There's something wrong.", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MsgResponse> call, Throwable t) {
                                        Snackbar.make(confirmCoor, "There's something wrong.", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                builder.setView(input, 50, 0, 50, 0);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (negativeButton != null) {
                    negativeButton.setBackgroundColor(Color.WHITE);
                    negativeButton.setTextColor(Color.BLACK);
                }

                if (positiveButton != null) {
                    positiveButton.setBackgroundColor(Color.WHITE);
                    positiveButton.setTextColor(Color.BLACK);
                }


            } else {
                Snackbar.make(confirmCoor, "No scan data received!", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(confirmCoor, "No scan data received!", Snackbar.LENGTH_SHORT).show();
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
