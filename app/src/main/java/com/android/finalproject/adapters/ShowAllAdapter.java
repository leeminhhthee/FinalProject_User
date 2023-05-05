package com.android.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.finalproject.R;
import com.android.finalproject.activities.DetailActivity;
import com.android.finalproject.models.ShowAllModel;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ViewHolder>{
    Context context;
    List<ShowAllModel> array;

    public ShowAllAdapter(Context context, List<ShowAllModel> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_showall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowAllModel showAllNewModel = array.get(position);
        holder.name.setText(showAllNewModel.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.price.setText(decimalFormat.format(showAllNewModel.getPrice()) + " VND");
        holder.brand.setText(showAllNewModel.getPro_brand());
        Glide.with(context).load(showAllNewModel.getImg_url()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("detailed", showAllNewModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, brand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_ProImgSeeall);
            name = itemView.findViewById(R.id.item_ProNameSeeall);
            price = itemView.findViewById(R.id.item_ProPriceSeeall);
            brand = itemView.findViewById(R.id.item_ProBrandSeeall);
        }
    }
}
