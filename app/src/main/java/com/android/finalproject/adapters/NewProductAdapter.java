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
import com.android.finalproject.fragments.SupportFragment;
import com.android.finalproject.models.NewProductModel;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder> {
    private Context context;
    private List<NewProductModel> array;

    public NewProductAdapter(Context context, List<NewProductModel> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_new, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewProductModel productNew = array.get(position);
        holder.txtName.setText(productNew.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtPrice.setText(decimalFormat.format(productNew.getPrice()) + " VND");
        holder.txtDescription.setText(productNew.getDescription());
        Glide.with(context).load(productNew.getImg_url()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("detailed", productNew);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtPrice, txtName, txtDescription;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPrice = itemView.findViewById(R.id.item_pro_price);
            txtName = itemView.findViewById(R.id.item_pro_name);
            img = itemView.findViewById(R.id.item_pro_image);
            txtDescription = itemView.findViewById(R.id.item_pro_description);
        }

    }
}
