package com.uhac.umpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uhac.umpay.R;

public class LoginActivity extends AppCompatActivity {

    CoordinatorLayout loginCoor;
    AppCompatButton scanCardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginCoor = (CoordinatorLayout) findViewById(R.id.loginCoor);
        scanCardBtn = (AppCompatButton) findViewById(R.id.scanCardBtn);

        scanCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(LoginActivity.this);
                scanIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningIntentResult != null) { //if result is null

            String scanContent = scanningIntentResult.getContents();
            String scanFormat = scanningIntentResult.getFormatName();

            if(scanContent != null && scanFormat != null) {
                Snackbar.make(loginCoor, "content: " + scanContent, Snackbar.LENGTH_INDEFINITE).show();
            } else {
                Snackbar.make(loginCoor, "No scan data received!", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(loginCoor, "No scan data received!", Snackbar.LENGTH_SHORT).show();
        }
    }
}
