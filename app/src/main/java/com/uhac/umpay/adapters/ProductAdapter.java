package com.uhac.umpay.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uhac.umpay.R;
import com.uhac.umpay.dialogs.OrderDialog;
import com.uhac.umpay.helpers.BitmapTransform;
import com.uhac.umpay.models.Product;
import com.uhac.umpay.models.ProductCart;
import com.uhac.umpay.models.ProductOrder;
import com.uhac.umpay.utilities.Caloocan;

import java.util.ArrayList;

/**
 * adapter for product recycler view
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    /* PICASSO Thing resize fast big load images haha */
    /* Credits: http://stackoverflow.com/questions/23740307/load-large-images-with-picasso-and-custom-transform-object */
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;
    private static final int SIZE = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    private CoordinatorLayout activityCoor;
    private ProductCart productCart;
    private ArrayList<Product> products;
    private Context c;
    private OrderDialog orderDialog;

    public ProductAdapter(CoordinatorLayout activityCoor, ArrayList<Product> products, ProductCart productCart, Context c) {
        this.activityCoor = activityCoor;
        this.products = products;
        this.productCart = productCart;
        this.c = c;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product, parent, false);

        return new ProductHolder(v);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final Product product = products.get(position);

        holder.prodNameTV.setText(product.getName());
        holder.prodCategTV.setText(product.getCategoryName());
        holder.prodDescTV.setText(product.getDescription());
        holder.prodQtyTV.setText("Remaining: " + product.getQty());
        holder.prodPriceTV.setText(Caloocan.PESO_SIGN + " " + Caloocan.numberFormat.format(product.getPrice()));

        Picasso.with(c).load("http://192.168.1.142/union_pay/" + product.getImg())
                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .skipMemoryCache()
                .resize(SIZE, SIZE)
                .centerInside().into(holder.prodImgIV, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.loadingBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.loadingBar.setVisibility(View.GONE);
                holder.prodImgIV.setImageResource(R.drawable.product);
            }
        });

        holder.prodLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;

                if (productCart.isExists(products.get(position))) {
                    qty = productCart.getProwareItemById(products.get(position).getId()).getQuantity();
                }

                orderDialog = OrderDialog.newInstance(qty, product.getOrderLimit(), product);
                orderDialog.show(((AppCompatActivity) c).getSupportFragmentManager(), OrderDialog.class.getName());

                orderDialog.setOnOkDismissListener(new OrderDialog.OnOkDismissListener() {
                    @Override
                    public void onDismiss(int qty) {
                        if (!productCart.addItem(new ProductOrder(product, qty))) {
                            Snackbar.make(activityCoor, productCart.getErrorMsg(), Snackbar.LENGTH_LONG).show();
                        } else if (productCart.isExists(product) || orderDialog.getQty() != 0) {
                            Snackbar.make(activityCoor, "Product Added to Cart.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        if (product.getQty() <= 0) { //check availability to strikethrough the item name
            holder.prodLin.setOnClickListener(new View.OnClickListener() { //set the on click to show that the item is not available
                @Override
                public void onClick(View v) {
                    Toast.makeText(c, "Item not available.", Toast.LENGTH_SHORT).show();
                }
            });
            holder.priceQtyLin.setBackgroundColor(c.getResources().getColor(R.color.materialRed));
        } else {
            holder.priceQtyLin.setBackgroundColor(c.getResources().getColor(R.color.colorYellow));
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        CardView prodLin;
        AppCompatTextView prodNameTV;
        AppCompatTextView prodDescTV;
        AppCompatTextView prodCategTV;
        LinearLayout priceQtyLin;
        AppCompatTextView prodQtyTV;
        AppCompatTextView prodPriceTV;
        ImageView prodImgIV;
        ProgressBar loadingBar;

        public ProductHolder(View itemView) {
            super(itemView);

            prodLin = (CardView) itemView.findViewById(R.id.prodLin);
            prodImgIV = (ImageView) itemView.findViewById(R.id.prodImgIV);
            prodNameTV = (AppCompatTextView) itemView.findViewById(R.id.prodNameTV);
            prodDescTV = (AppCompatTextView) itemView.findViewById(R.id.prodDescTV);
            prodCategTV = (AppCompatTextView) itemView.findViewById(R.id.prodCategTV);
            priceQtyLin = (LinearLayout) itemView.findViewById(R.id.priceQtyLin);
            prodQtyTV = (AppCompatTextView) itemView.findViewById(R.id.prodQtyTV);
            prodPriceTV = (AppCompatTextView) itemView.findViewById(R.id.prodPriceTV);
            loadingBar = (ProgressBar) itemView.findViewById(R.id.loadingBar);
        }
    }
}
