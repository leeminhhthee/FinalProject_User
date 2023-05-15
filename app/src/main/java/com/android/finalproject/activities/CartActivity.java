package com.android.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.adapters.MyCartAdapter;
import com.android.finalproject.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    Toolbar toolbarMyCart;
    TextView emptyCart, tvTotal, item_CartProName;
    RecyclerView rc_cart;
    ImageView removeCart;
    List<MyCartModel> myCartModelList;
    MyCartAdapter myCartAdapter;
    AppCompatButton btnCheckOut;

    int totalPrice;
    int totalBill;
    boolean isAliveCart = true;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //get data from my cart adapter
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("MyTotalPrice"));

        initView();
        ActionToolbar();
        getDataCart();
    }

    private void getDataCart() {
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                emptyCart.setVisibility(View.VISIBLE);
                                isAliveCart = false;
                                return;
                            }
                            toolbarMyCart.setTitle("My Cart (" + task.getResult().size() + ")");
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                myCartModelList.add(myCartModel);
                                myCartAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            totalBill = intent.getIntExtra("totalPrice", 0);
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            tvTotal.setText(decimalFormat.format(totalBill) + " VND");
        }
    };

    private void initView() {
        toolbarMyCart = findViewById(R.id.toolbarMyCart);
        emptyCart = findViewById(R.id.tvCartEmpty);
        tvTotal = findViewById(R.id.tvTotal);
        rc_cart = findViewById(R.id.rc_productCart);
        rc_cart.setLayoutManager(new LinearLayoutManager(this));
        myCartModelList = new ArrayList<>();
        myCartAdapter = new MyCartAdapter(this, myCartModelList);
        rc_cart.setAdapter(myCartAdapter);

        btnCheckOut = findViewById(R.id.btnCheckOut);
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAliveCart) {
                    Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                    intent.putExtra("total", totalBill);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbarMyCart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarMyCart.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}