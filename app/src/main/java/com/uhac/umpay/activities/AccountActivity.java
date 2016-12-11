package com.uhac.umpay.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uhac.umpay.R;
import com.uhac.umpay.models.MsgResponse;
import com.uhac.umpay.networks.RetroClient;
import com.uhac.umpay.networks.RetroInterface;
import com.uhac.umpay.utilities.Caloocan;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * account options for user
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class AccountActivity extends AppCompatActivity {

    private String accountNo;

    RetroInterface retroInterface;

    private CoordinatorLayout accountCoor;

    private LinearLayout checkBalanceLin;
    private LinearLayout lockAccountLin;

    private AppCompatTextView accountNameTV;
    private AppCompatTextView checkBalanceMsgTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        IntentIntegrator scanIntegrator = new IntentIntegrator(AccountActivity.this);
        scanIntegrator.initiateScan();

        retroInterface = RetroClient.getClient(AccountActivity.this).create(RetroInterface.class);

        accountCoor = (CoordinatorLayout) findViewById(R.id.accountCoor);
        checkBalanceLin = (LinearLayout) findViewById(R.id.checkBalanceLin);
        lockAccountLin = (LinearLayout) findViewById(R.id.lockAccountLin);
        accountNameTV = (AppCompatTextView) findViewById(R.id.accountNameTV);
        checkBalanceMsgTV = (AppCompatTextView) findViewById(R.id.checkBalanceMsgTV);

        lockAccountLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate PIN
                final AppCompatEditText input = new AppCompatEditText(AccountActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder
                        .setTitle("Enter Password")
                        .setMessage("Please enter your password for verification.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Call<MsgResponse> call = retroInterface.lockAccount(accountNo);
                                call.enqueue(new Callback<MsgResponse>() {
                                    @Override
                                    public void onResponse(Call<MsgResponse> call, Response<MsgResponse> response) {
                                        if (response.body() != null) {
                                            if (response.body().getResponse_code() == 1) {
                                                Toast.makeText(AccountActivity.this, response.body().getResponse_msg(), Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            } else if (response.body().getResponse_code() == -1) {
                                                Snackbar.make(accountCoor, response.body().getResponse_msg(), Snackbar.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Snackbar.make(accountCoor, "Something wrong.", Snackbar.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MsgResponse> call, Throwable t) {
                                        Snackbar.make(accountCoor, "Something wrong.", Snackbar.LENGTH_LONG).show();
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

            }
        });

        checkBalanceLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBalanceMsgTV.setText("Checking...");
                Call<MsgResponse> call = retroInterface.checkBalance(accountNo);
                call.enqueue(new Callback<MsgResponse>() {
                    @Override
                    public void onResponse(Call<MsgResponse> call, Response<MsgResponse> response) {
                        checkBalanceMsgTV.setText("Check your balance.");
                        if (response.body() != null) {
                            if (response.body().getResponse_code() == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                                builder
                                        .setTitle("Card Balance")
                                        .setMessage("You Balance is: " + Caloocan.PESO_SIGN + " " + response.body().getResponse_msg())
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                                if (negativeButton != null) {
                                    negativeButton.setBackgroundColor(Color.WHITE);
                                    negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                }
                            } else if (response.body().getResponse_code() == -1) {
                                Snackbar.make(accountCoor, "Something wrong.", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(accountCoor, "Something wrong.", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MsgResponse> call, Throwable t) {
                        Snackbar.make(accountCoor, "Something wrong.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningIntentResult != null) { //if result is not null

            String scanContent = scanningIntentResult.getContents(); //account number
            String scanFormat = scanningIntentResult.getFormatName();

            if(scanContent != null && scanFormat != null) {
                accountNo = scanContent;
                Call<MsgResponse> call = retroInterface.checkStatus(scanContent);

                call.enqueue(new Callback<MsgResponse>() {
                    @Override
                    public void onResponse(Call<MsgResponse> call, Response<MsgResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getResponse_code() == 1) {
                                accountNameTV.setText(Caloocan.capitalizeAll(response.body().getResponse_msg())); //set name
                            } else if (response.body().getResponse_code() == -2) {
                                Toast.makeText(AccountActivity.this, "Your account is locked. Please call/go to Union Bank.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else if (response.body().getResponse_code() == -1) {
                                Toast.makeText(AccountActivity.this, "Invalid Account.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(AccountActivity.this, "Something wrong.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MsgResponse> call, Throwable t) {
                        Toast.makeText(AccountActivity.this, "Something wrong.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

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
