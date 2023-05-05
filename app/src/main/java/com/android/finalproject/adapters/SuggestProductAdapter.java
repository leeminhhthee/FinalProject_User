package com.android.finalproject.adapters;

import android.annotation.SuppressLint;
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
import com.android.finalproject.models.SuggestProductModel;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class SuggestProductAdapter extends RecyclerView.Adapter<SuggestProductAdapter.ViewHolder> {
    private Context context;
    private List<SuggestProductModel> array;

    public SuggestProductAdapter(Context context, List<SuggestProductModel> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_suggest, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuggestProductModel suggestProductModel = array.get(position);
        holder.productNameSug.setText(suggestProductModel.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.productPriceSug.setText("đ"+decimalFormat.format(suggestProductModel.getPrice()));
        Glide.with(context).load(suggestProductModel.getImg_url()).into(holder.productImgSug);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("detailed", suggestProductModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView productNameSug, productPriceSug;
        ImageView productImgSug;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameSug = itemView.findViewById(R.id.productNameSug);
            productPriceSug = itemView.findViewById(R.id.productPriceSug);
            productImgSug = itemView.findViewById(R.id.productImgSug);
        }

    }
}
