package com.uhac.umpay.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.uhac.umpay.R;
import com.uhac.umpay.models.Product;
import com.uhac.umpay.utilities.Caloocan;

/**
 * how many you want to order on proware dialog
 * Created by Exequiel Egbert V. Ponce on 9/10/2016.
 */
public class OrderDialog extends AppCompatDialogFragment {

    AppCompatTextView prodNameTV;
    AppCompatTextView howManyTV;
    AppCompatTextView orderLimitTV;

    private int qty;
    private int orderLimit;
    private Product product;

    private OnOkDismissListener onOkDismissListener;

    public OrderDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static OrderDialog newInstance(int qty, int orderLimit, Product product) {
        OrderDialog prowareOrderDialog = new OrderDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("qty", qty);
        bundle.putInt("orderLimit", orderLimit);
        bundle.putParcelable("product", product);

        prowareOrderDialog.setArguments(bundle);

        return prowareOrderDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_order, null);

        prodNameTV = (AppCompatTextView) v.findViewById(R.id.prodNameTV);
        howManyTV = (AppCompatTextView) v.findViewById(R.id.howManyTV);
        orderLimitTV = (AppCompatTextView) v.findViewById(R.id.orderLimitTV);
        AppCompatImageView minusIV = (AppCompatImageView) v.findViewById(R.id.minusIV);
        AppCompatImageView addIV = (AppCompatImageView) v.findViewById(R.id.addIV);

        Bundle bundle = getArguments();

        if (bundle != null) {
            qty = bundle.getInt("qty");
            orderLimit = bundle.getInt("orderLimit");
            product = bundle.getParcelable("product");

            if (orderLimit != -1) {
                orderLimitTV.setText("(Reservations for this product are limited to " + orderLimit + " per item only).");
            }
        } else {
            qty = 0;
            product = null;
        }

        prodNameTV.setText("Reserve for " + product.getName() + " " + Caloocan.PESO_SIGN + " " + Caloocan.numberFormat.format(product.getPrice()));
        howManyTV.setText(String.valueOf(qty));

        addIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((qty < orderLimit || orderLimit == -1) && qty < product.getQty()) {
                    qty++;
                }

                howManyTV.setText(String.valueOf(qty));
            }
        });

        minusIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty > 0) {
                    qty--;
                }

                howManyTV.setText(String.valueOf(qty));
            }
        });

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle("Order Item")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (product != null) {
                            onOkDismissListener.onDismiss(qty);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        b.setView(v);
        return b.create();
    }

    public void setOnOkDismissListener(OnOkDismissListener onOkDismissListener) {
        this.onOkDismissListener = onOkDismissListener;
    }

    public int getQty() {
        return qty;
    }

    public interface OnOkDismissListener {
        void onDismiss(int qty);
    }
}
