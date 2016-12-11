package com.uhac.umpay.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uhac.umpay.R;
import com.uhac.umpay.models.ProductCart;
import com.uhac.umpay.utilities.Caloocan;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * pending items on the dialog
 * Created by Exequiel Egbert V. Ponce on 7/28/2016.
 */
public class PendingProwareAdapter extends RecyclerView.Adapter<PendingProwareAdapter.ViewHolder> {

    private ProductCart productCart;
    private AppCompatTextView totalPriceTV;

    static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout prowarePendingLin;
        public TextView itemNameTV;
        public TextView itemQtyTV;
        public TextView totalPriceTV;

        public ViewHolder(View itemView) {

            super(itemView);

            prowarePendingLin = (LinearLayout) itemView.findViewById(R.id.prowarePendingLin);
            itemNameTV = (TextView) itemView.findViewById(R.id.itemNameTV);
            itemQtyTV = (TextView) itemView.findViewById(R.id.itemQtyTV);
            totalPriceTV = (TextView) itemView.findViewById(R.id.totalPriceTV);
        }
    }

    public PendingProwareAdapter(AppCompatTextView totalPriceTV, ProductCart productCart) {
        this.productCart = productCart;
        this.totalPriceTV = totalPriceTV;
    }

    @Override
    public PendingProwareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_order, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PendingProwareAdapter.ViewHolder holder, final int position) {
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");

        holder.itemNameTV.setText(productCart.getProductItem(position).getProductItem().getName());
        holder.itemQtyTV.setText("x" + productCart.getProductItem(position).getQuantity());
        holder.totalPriceTV.setText(Caloocan.PESO_SIGN + " " + numberFormat.format(( //multiply price and quantity ordered
                productCart.getProductItem(position).getProductItem().getPrice() * productCart.getProductItem(position).getQuantity())));

        holder.prowarePendingLin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                productCart.removeItem(position);

                notifyDataSetChanged();

                totalPriceTV.setText("Total: " + Caloocan.PESO_SIGN + " " + Caloocan.numberFormat.format(productCart.getTotalCartPrice()));

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return productCart.getCartSize();
    }
}
