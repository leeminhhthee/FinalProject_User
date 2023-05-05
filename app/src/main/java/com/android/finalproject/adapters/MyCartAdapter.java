package com.android.finalproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.finalproject.R;
import com.android.finalproject.activities.CartActivity;
import com.android.finalproject.activities.MainActivity;
import com.android.finalproject.models.MyCartModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder>{
    Context context;
    List<MyCartModel> array;
    int totalPrice = 0;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public MyCartAdapter(Context context, List<MyCartModel> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCartModel myCartModel = array.get(position);
        holder.name.setText(myCartModel.getProductName());
        holder.totalQty.setText(myCartModel.getTotalQty()+"");
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.price.setText(decimalFormat.format(myCartModel.getProductPrice()));
        holder.totalPrice.setText(decimalFormat.format(myCartModel.getTotalPrice()));
        Glide.with(context).load(myCartModel.getProductImg()).into(holder.image);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        holder.removeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                        MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                        if(myCartModel.getProductName().equals(holder.name.getText().toString())){
                                            doc.getReference().delete();
                                        }
                                    }
                                }
                            }
                        });
                Toast.makeText(view.getContext(), "DONE", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, MainActivity.class));
                ((Activity) context).finish();
            }
        });

        totalPrice = totalPrice + myCartModel.getTotalPrice();
        Intent intent = new Intent("MyTotalPrice");
        intent.putExtra("totalPrice", totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, totalQty, totalPrice;
        ImageView image, removeCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_CartProName);
            price = itemView.findViewById(R.id.item_CartProPrice);
            totalQty = itemView.findViewById(R.id.item_CartProQty);
            totalPrice = itemView.findViewById(R.id.item_CartTotalPrice);
            image = itemView.findViewById(R.id.item_CartProImg);
            removeCart = itemView.findViewById(R.id.removeCart);
        }
    }
}
