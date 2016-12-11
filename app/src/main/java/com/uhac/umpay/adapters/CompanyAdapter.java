package com.uhac.umpay.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.uhac.umpay.R;
import com.uhac.umpay.activities.BarcodeActivity;
import com.uhac.umpay.helpers.BitmapTransform;
import com.uhac.umpay.models.Company;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for company list
 * Created by Exequiel Egbert V. Ponce on 12/10/2016.
 */

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder> {

    /* PICASSO Thing resize fast big load images haha */
    /* Credits: http://stackoverflow.com/questions/23740307/load-large-images-with-picasso-and-custom-transform-object */
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;
    private static final int SIZE = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    private ArrayList<Company> companies;
    private Context c;
    private OnCompanyClickListener onCompanyClickListener;

    public CompanyAdapter(Context c, ArrayList<Company> companies, OnCompanyClickListener onCompanyClickListener) {
        this.companies = companies;
        this.c = c;
        this.onCompanyClickListener = onCompanyClickListener;
    }

    @Override
    public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_company, parent, false);

        return new CompanyHolder(v);
    }

    @Override
    public void onBindViewHolder(final CompanyHolder holder, int position) {
        final Company company = companies.get(position);

        Picasso.with(c).load("http://192.168.1.142/union_pay/" + company.getImg())
                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .skipMemoryCache()
                .resize(SIZE, SIZE)
                .centerInside().into(holder.companyImageIV, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                holder.companyImageIV.setImageResource(R.drawable.building);
            }
        });

        holder.companyNameTV.setText(company.getName());
        holder.companyDescTV.setText(company.getDesc());
        holder.companyAddressTV.setText(company.getAddress());

        holder.companyLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompanyClickListener.companyClick(company);
            }
        });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    class CompanyHolder extends RecyclerView.ViewHolder {

        LinearLayout companyLin;
        CircleImageView companyImageIV;
        AppCompatTextView companyNameTV;
        AppCompatTextView companyDescTV;
        AppCompatTextView companyAddressTV;

        CompanyHolder(View itemView) {
            super(itemView);

            companyLin = (LinearLayout) itemView.findViewById(R.id.companyLin);
            companyImageIV = (CircleImageView) itemView.findViewById(R.id.companyImageIV);
            companyNameTV = (AppCompatTextView) itemView.findViewById(R.id.companyNameTV);
            companyDescTV = (AppCompatTextView) itemView.findViewById(R.id.companyDescTV);
            companyAddressTV = (AppCompatTextView) itemView.findViewById(R.id.companyAddressTV);
        }
    }

    public interface OnCompanyClickListener {
        void companyClick(Company company);
    }
}
