package com.uhac.umpay.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uhac.umpay.R;
import com.uhac.umpay.models.ProductCart;
import com.uhac.umpay.models.ProductOrder;
import com.uhac.umpay.utilities.Caloocan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 *
 * Created by Exequiel Egbert V. Ponce on 7/28/2016.
 */
public class ReserveAdapter extends RecyclerView.Adapter<ReserveAdapter.ReserveHolder> {

    private ProductCart productCart = null;
    private ArrayList<ProductOrder> productOrders = null;

    private boolean isCart;

    public class ReserveHolder extends RecyclerView.ViewHolder {

        public TextView itemNameTV;
        public TextView itemQtyTV;
        public TextView totalPriceTV;

        public ReserveHolder(View itemView) {
            super(itemView);

            itemNameTV = (TextView) itemView.findViewById(R.id.itemNameTV);
            itemQtyTV = (TextView) itemView.findViewById(R.id.itemQtyTV);
            totalPriceTV = (TextView) itemView.findViewById(R.id.totalPriceTV);
        }
    }

    public ReserveAdapter(ProductCart productCart) {
        this.productCart = productCart;
        isCart = true;
    }
    public ReserveAdapter(ArrayList<ProductOrder> productOrders) {
        this.productOrders = productOrders;
        isCart = false;
    }

    @Override
    public ReserveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_reserve, parent, false);

        return new ReserveHolder(v);
    }

    @Override
    public void onBindViewHolder(ReserveHolder holder, final int position) {
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");

        if (isCart) { //if data is by cart
            holder.itemNameTV.setText(productCart.getProductItem(position).getProductItem().getName());
            holder.itemQtyTV.setText("x" + productCart.getProductItem(position).getQuantity());
            holder.totalPriceTV.setText(Caloocan.PESO_SIGN + " " + numberFormat.format(( //multiply price and quantity ordered
                    productCart.getProductItem(position).getProductItem().getPrice() * productCart.getProductItem(position).getQuantity())));
        } else {
            holder.itemNameTV.setText(productOrders.get(position).getProductItem().getName());
            holder.itemQtyTV.setText("x" + productOrders.get(position).getQuantity());
            holder.totalPriceTV.setText(Caloocan.PESO_SIGN + " " + numberFormat.format(( //multiply price and quantity ordered
                    productOrders.get(position).getProductItem().getPrice() * productOrders.get(position).getQuantity())));
        }
    }

    @Override
    public int getItemCount() {
        if (isCart)
            return productCart.getCartSize();
        else
            return productOrders.size();
    }
}
