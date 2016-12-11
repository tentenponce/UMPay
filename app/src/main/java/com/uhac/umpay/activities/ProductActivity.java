package com.uhac.umpay.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.uhac.umpay.R;
import com.uhac.umpay.adapters.ProductAdapter;
import com.uhac.umpay.dialogs.OrderDialog;
import com.uhac.umpay.dialogs.ProductCartDialog;
import com.uhac.umpay.models.Company;
import com.uhac.umpay.models.Product;
import com.uhac.umpay.models.ProductCart;
import com.uhac.umpay.models.ProductOrder;
import com.uhac.umpay.networks.RetroClient;
import com.uhac.umpay.networks.RetroInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * list of items
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class ProductActivity extends AppCompatActivity {

    private ArrayList<Product> products = new ArrayList<>();

    private ProductCartDialog productCartDialog;

    private Company company = null;

    private ProductCart productCart;

    private OrderDialog orderDialog;

    CoordinatorLayout productCoor;

    SwipeRefreshLayout productSwipe;

    AppCompatButton scanBtn;

    RecyclerView productRecyclerView;

    ProductAdapter productAdapter;

    FloatingActionButton cartFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        company = getIntent().getParcelableExtra("company");
        setTitle("Products of " + company.getName());

        productCoor = (CoordinatorLayout) findViewById(R.id.productCoor);
        productSwipe = (SwipeRefreshLayout) findViewById(R.id.productSwipe);
        scanBtn = (AppCompatButton) findViewById(R.id.scanBtn);
        productRecyclerView = (RecyclerView) findViewById(R.id.productRecyclerView);
        cartFab = (FloatingActionButton) findViewById(R.id.cartFab);

        productCart = new ProductCart();
        productCart.emptyCart();

        productAdapter = new ProductAdapter(productCoor, products, productCart, ProductActivity.this);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int spanCount = 2;
                int spacing = getResources().getDimensionPixelSize(R.dimen.product_margin_grid); //spacing between views in grid

                if (position >= 0) {
                    int column = position % spanCount; // item column

                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });
        productRecyclerView.setAdapter(productAdapter);

        productSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });

        cartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCartDialog = new ProductCartDialog(ProductActivity.this, productCart); //create new instance to renew cart list
                productCartDialog.setPositiveButton("Reserve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!productCart.isEmpty()) {
                            Intent intent = new Intent(ProductActivity.this, ReservationActivity.class);
                            intent.putExtra("productCart", productCart);
                            startActivity(intent);
                        } else {
                            productCartDialog.closeDialog();
                            Snackbar.make(productCoor, "Your cart is empty.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                productCartDialog.showAlertDialog();
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(ProductActivity.this);
                scanIntegrator.initiateScan();
            }
        });

        loadProducts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningIntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningIntentResult != null) { //if result is not null

            String scanContent = scanningIntentResult.getContents();
            String scanFormat = scanningIntentResult.getFormatName();

            if(scanContent != null && scanFormat != null) {
                scanItem(searchItem(scanContent.trim()));
            } else {
                Snackbar.make(productCoor, "No scan data received!", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(productCoor, "No scan data received!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void scanItem(final Product product) {
        if (product == null) {
            Snackbar.make(productCoor, "Product does not exist on this company.", Snackbar.LENGTH_LONG).show();
            return;
        }

        int qty = 0;

        if (productCart.isExists(product)) {
            qty = productCart.getProwareItemById(product.getId()).getQuantity();
        }

        orderDialog = OrderDialog.newInstance(qty, product.getOrderLimit(), product);
        orderDialog.show((ProductActivity.this).getSupportFragmentManager(), OrderDialog.class.getName());

        orderDialog.setOnOkDismissListener(new OrderDialog.OnOkDismissListener() {
            @Override
            public void onDismiss(int qty) {
                if (!productCart.addItem(new ProductOrder(product, qty))) {
                    Snackbar.make(productCoor, productCart.getErrorMsg(), Snackbar.LENGTH_LONG).show();
                } else if (productCart.isExists(product) || orderDialog.getQty() != 0) {
                    Snackbar.make(productCoor, "Product Added to Cart.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private Product searchItem(String productCode) {
        for (Product product : products) {
            if (product.getCode().equals(productCode)) {
                return product;
            }
        }
        return null;
    }

    private void loadProducts() {
        productSwipe.post(new Runnable() {
            @Override
            public void run() {
                productSwipe.setRefreshing(true);
            }
        });

        if (company != null) {
            RetroInterface retroInterface = RetroClient.getClient(ProductActivity.this).create(RetroInterface.class);
            Call<ArrayList<Product>> call = retroInterface.getProducts(company.getId());

            call.enqueue(new Callback<ArrayList<Product>>() {
                @Override
                public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                    if (response.body() != null) {
                        products.clear();
                        products.addAll(response.body());

                        productAdapter.notifyDataSetChanged();
                    }

                    productSwipe.post(new Runnable() {
                        @Override
                        public void run() {
                            productSwipe.setRefreshing(false);
                        }
                    });

                }

                @Override
                public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                    Snackbar.make(productCoor, "There's something wrong.", Snackbar.LENGTH_SHORT).show();

                    productSwipe.post(new Runnable() {
                        @Override
                        public void run() {
                            productSwipe.setRefreshing(false);
                        }
                    });
                }
            });
        } else {
            Toast.makeText(ProductActivity.this, "Please pick a company.", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
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
