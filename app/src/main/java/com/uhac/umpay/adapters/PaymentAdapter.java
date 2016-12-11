package com.uhac.umpay.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uhac.umpay.R;
import com.uhac.umpay.activities.BarcodeActivity;
import com.uhac.umpay.models.PaymentGroup;
import com.uhac.umpay.models.ProductOrder;
import com.uhac.umpay.utilities.Caloocan;

import java.util.ArrayList;

/**
 *
 * Created by Exequiel Egbert V. Ponce on 8/9/2016.
 */
public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PendingViewHolder> {

    private ArrayList<PaymentGroup> paymentGroups;
    private Context c;

    public class PendingViewHolder extends RecyclerView.ViewHolder {

        LinearLayout reservationLin;
        AppCompatTextView reserveCompanyTV;
        AppCompatTextView reserveCodeTV;
        LinearLayout reservationItemListLin;
        AppCompatTextView reserveTypeTV;
        AppCompatTextView totalReservePriceTV;
        AppCompatTextView dateAgoTV;
        Context context;

        public PendingViewHolder(View itemView, Context context) {
            super(itemView);

            reservationLin = (LinearLayout) itemView.findViewById(R.id.reservationLin);
            reserveCompanyTV = (AppCompatTextView) itemView.findViewById(R.id.reserveCompanyTV);
            reserveCodeTV = (AppCompatTextView) itemView.findViewById(R.id.reserveCodeTV);
            reserveTypeTV = (AppCompatTextView) itemView.findViewById(R.id.reserveTypeTV);
            reservationItemListLin = (LinearLayout) itemView.findViewById(R.id.reservationItemListLin);
            totalReservePriceTV = (AppCompatTextView) itemView.findViewById(R.id.totalReservePriceTV);
            dateAgoTV = (AppCompatTextView) itemView.findViewById(R.id.dateAgoTV);
            this.context = context;
        }

        private void setReservationitems(ArrayList<ProductOrder> productOrders) {
            reservationItemListLin.removeAllViews();

            for (ProductOrder productOrder : productOrders) {
                ReservationItemLayout reservationItemLayout = new ReservationItemLayout(context);
                reservationItemLayout.itemNameTV.setText(productOrder.getProductItem().getName());
                reservationItemLayout.itemQtyTV.setText("x" + productOrder.getQuantity());
                reservationItemLayout.totalPriceTV.setText(Caloocan.PESO_SIGN + " " +
                        Caloocan.numberFormat.format(productOrder.getQuantity() * productOrder.getProductItem().getPrice()));
                reservationItemListLin.addView(reservationItemLayout);
            }
        }

        private double getTotalOrderPrice(ArrayList<ProductOrder> productOrders) {
            double orderPrice = 0;
            for (int i = 0; i < productOrders.size(); i++) {
                double itemPrice = productOrders.get(i).getProductItem().getPrice();
                int qty = productOrders.get(i).getQuantity();

                orderPrice += itemPrice * qty;
            }

            return orderPrice;
        }

        //layout for per item reserved
        class ReservationItemLayout extends LinearLayout {

            AppCompatTextView itemNameTV;
            AppCompatTextView itemQtyTV;
            AppCompatTextView totalPriceTV;

            public ReservationItemLayout(final Context context) {
                super(context);

                inflate(context, R.layout.layout_my_reservation_item, this);

                itemNameTV = (AppCompatTextView) findViewById(R.id.itemNameTV);
                itemQtyTV = (AppCompatTextView) findViewById(R.id.itemQtyTV);
                totalPriceTV = (AppCompatTextView) findViewById(R.id.totalPriceTV);
            }
        }
    }

    public PaymentAdapter(Context c, ArrayList<PaymentGroup> paymentGroups) {
        this.paymentGroups = paymentGroups;
        this.c = c;
    }

    @Override
    public PaymentAdapter.PendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_my_reservation, parent, false);

        return new PendingViewHolder(v, parent.getContext());
    }

    @Override
    public void onBindViewHolder(PaymentAdapter.PendingViewHolder holder, final int position) {
        holder.reserveTypeTV.setVisibility(View.GONE);
        holder.reserveCompanyTV.setText(paymentGroups.get(position).getCompanyName());
        holder.reserveCodeTV.setText(paymentGroups.get(position).getReserveCode());
        holder.setReservationitems(paymentGroups.get(position).getProductOrders());
        holder.totalReservePriceTV.setText(
                Caloocan.PESO_SIGN + " " +
                        Caloocan.numberFormat.format(
                                holder.getTotalOrderPrice(paymentGroups.get(position).getProductOrders())
                        )
        );

        holder.dateAgoTV.setText(Caloocan.dateDifference(
                Caloocan.FORMATTER.format(paymentGroups.get(position).getDateToday()),
                Caloocan.FORMATTER.format(paymentGroups.get(position).getReservedDate())));
    }

    @Override
    public int getItemCount() {
        return paymentGroups.size();
    }
}
