package com.uhac.umpay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;
import com.uhac.umpay.R;
import com.uhac.umpay.adapters.CompanyAdapter;
import com.uhac.umpay.models.Company;
import com.uhac.umpay.networks.RetroClient;
import com.uhac.umpay.networks.RetroInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * pick a company
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class CompanyActivity extends AppCompatActivity {

    ArrayList<Company> companies = new ArrayList<>();

    CoordinatorLayout companyCoor;

    SwipeRefreshLayout companySwipe;

    RecyclerView companyRecyclerView;

    CompanyAdapter companyAdapter;

    RetroInterface retroInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        setTitle("Choose a Company");

        retroInterface = RetroClient.getClient(CompanyActivity.this).create(RetroInterface.class);

        companyCoor = (CoordinatorLayout) findViewById(R.id.companyCoor);
        companySwipe = (SwipeRefreshLayout) findViewById(R.id.companySwipe);
        companyRecyclerView = (RecyclerView) findViewById(R.id.companyRecyclerView);

        companyAdapter = new CompanyAdapter(CompanyActivity.this, companies, new CompanyAdapter.OnCompanyClickListener() {
            @Override
            public void companyClick(Company company) {
                Intent intent = new Intent(CompanyActivity.this, ProductActivity.class);
                intent.putExtra("company", company);
                startActivity(intent);
            }
        });
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(CompanyActivity.this));
        companyRecyclerView.setAdapter(companyAdapter);

        companySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCompanies();

            }
        });

        loadCompanies();
    }

    private void loadCompanies() {
        companySwipe.post(new Runnable() {
            @Override
            public void run() {
                companySwipe.setRefreshing(true);
            }
        });

        Call<ArrayList<Company>> call = retroInterface.getCompanies();

        call.enqueue(new Callback<ArrayList<Company>>() {
            @Override
            public void onResponse(Call<ArrayList<Company>> call, Response<ArrayList<Company>> response) {

                if (response.body() != null) {
                    companies.clear();
                    companies.addAll(response.body());

                    companyAdapter.notifyDataSetChanged();
                }

                companySwipe.post(new Runnable() {
                    @Override
                    public void run() {
                        companySwipe.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onFailure(Call<ArrayList<Company>> call, Throwable t) {
                Snackbar.make(companyCoor, "There's something wrong.", Snackbar.LENGTH_SHORT).show();

                companySwipe.post(new Runnable() {
                    @Override
                    public void run() {
                        companySwipe.setRefreshing(false);
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_company, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        IntentIntegrator scanIntegrator;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reserve_list:
                startActivity(new Intent(CompanyActivity.this, MyReservationActivity.class));
                return true;
            case R.id.action_cob:
                startActivity(new Intent(CompanyActivity.this, AccountActivity.class));
                return true;
            case R.id.action_payment:
                startActivity(new Intent(CompanyActivity.this, MyTransactionActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
