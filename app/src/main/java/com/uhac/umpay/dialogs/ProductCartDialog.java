package com.uhac.umpay.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.uhac.umpay.R;
import com.uhac.umpay.adapters.PendingProwareAdapter;
import com.uhac.umpay.models.ProductCart;
import com.uhac.umpay.utilities.Caloocan;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * Created by Exequiel Egbert V. Ponce on 7/28/2016.
 */
public class ProductCartDialog extends AlertDialog.Builder {

    Context c;
    RecyclerView recyclerView;
    AppCompatTextView totalPriceTV;
    AlertDialog alertDialog;

    public ProductCartDialog(Context context, ProductCart productCart) {
        super(context, R.style.AppCompatAlertDialogStyle);

        c = context;

        View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_product_pending, null);
        setView(view);

        setTitle("Pending Reservations");
        setMessage("Note: Long press to remove pending reservation");
        setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog = create();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        totalPriceTV = (AppCompatTextView) view.findViewById(R.id.totalPriceTV);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new PendingProwareAdapter(totalPriceTV, productCart));

        NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        totalPriceTV.setText("Total: " + Caloocan.PESO_SIGN + " " + numberFormat.format(productCart.getTotalCartPrice()));
    }

    public void showAlertDialog() {
        alertDialog = create();
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

    public void closeDialog() {
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
