package com.uhac.umpay.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uhac.umpay.helpers.BitmapTransform;

/**
 * barcode fullscreen
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class BarcodeActivity extends AppCompatActivity {

    private static final String QR_CODE_SITE = "https://chart.googleapis.com/chart?chs=300x300&cht=qr&choe=UTF-8&chl=";
    private static final String BARCODE_SITE = "http://barcode.tec-it.com/barcode.ashx?translate-esc=off&code=Code128&data=";

    /* PICASSO Thing resize fast big load images haha */
    /* Credits: http://stackoverflow.com/questions/23740307/load-large-images-with-picasso-and-custom-transform-object */
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;
    private static final int SIZE = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    private AppCompatTextView msgTV;
    private ImageView barcodeIV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String reserveCode = getIntent().getStringExtra("reserve_code");

        barcodeIV = new ImageView(BarcodeActivity.this);
        msgTV = new AppCompatTextView(BarcodeActivity.this);

        msgTV.setText("Loading");

        setContentView(msgTV);

        loadCode(reserveCode, BARCODE_SITE);
    }

    private void loadCode(final String reserveCode, final String path) {
        Picasso.with(BarcodeActivity.this).load(path + reserveCode)
                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .skipMemoryCache()
                .resize(SIZE, SIZE)
                .centerInside().into(barcodeIV, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                barcodeIV.setPadding(150, 150, 150, 150);
                setContentView(barcodeIV);

                barcodeIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (path.equals(BARCODE_SITE)) {
                            loadCode(reserveCode, QR_CODE_SITE);
                        } else {
                            loadCode(reserveCode, BARCODE_SITE);
                        }
                    }
                });
            }

            @Override
            public void onError() {
                msgTV.setText("Problem with your internet");
                setContentView(msgTV);
            }
        });
    }
}
