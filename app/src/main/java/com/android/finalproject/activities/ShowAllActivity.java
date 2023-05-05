package com.android.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.adapters.ShowAllAdapter;
import com.android.finalproject.adapters.SuggestProductAdapter;
import com.android.finalproject.models.ShowAllModel;
import com.android.finalproject.models.SuggestProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowAllActivity extends AppCompatActivity {
    RecyclerView rc_product;
    ShowAllAdapter showAllAdapter;
    SuggestProductAdapter suggestProductAdapter;
    List<ShowAllModel> showAllNewModelList;
    List<SuggestProductModel> suggestProductModelList;
    Toolbar toolbarNew;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        String brand = getIntent().getStringExtra("brand");

        firestore = FirebaseFirestore.getInstance();

        rc_product = findViewById(R.id.rc_product);
        toolbarNew = findViewById(R.id.toolbarNew);
        ActionToolbar();

        //Get all new products home
        if(brand == null || brand.isEmpty()){
            showAllNewModelList = new ArrayList<>();
            showAllAdapter = new ShowAllAdapter(this, showAllNewModelList);
            rc_product.setLayoutManager(new GridLayoutManager(this,1));
            rc_product.setAdapter(showAllAdapter);

            firestore.collection("NewProducts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ShowAllModel showAllNewModel = document.toObject(ShowAllModel.class);
                                    showAllNewModelList.add(showAllNewModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        //Get all products belongs to brand Apple
        if(brand != null && brand.equalsIgnoreCase("Apple")) {
            suggestProductModelList = new ArrayList<>();
            suggestProductAdapter = new SuggestProductAdapter(this, suggestProductModelList);
            rc_product.setLayoutManager(new GridLayoutManager(this,2));
            rc_product.setAdapter(suggestProductAdapter);
            toolbarNew.setTitle("Apple".toUpperCase(Locale.ROOT));

            firestore.collection("AllProducts").whereEqualTo("pro_brand", "Apple")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    SuggestProductModel suggestProductModel = document.toObject(SuggestProductModel.class);
                                    suggestProductModelList.add(suggestProductModel);
                                    suggestProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        //Get all products belongs to brand Samsung
        if(brand != null && brand.equalsIgnoreCase("Samsung")) {
            suggestProductModelList = new ArrayList<>();
            suggestProductAdapter = new SuggestProductAdapter(this, suggestProductModelList);
            rc_product.setLayoutManager(new GridLayoutManager(this,2));
            rc_product.setAdapter(suggestProductAdapter);
            toolbarNew.setTitle("Samsung".toUpperCase(Locale.ROOT));

            firestore.collection("AllProducts").whereEqualTo("pro_brand", "Samsung")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    SuggestProductModel suggestProductModel = document.toObject(SuggestProductModel.class);
                                    suggestProductModelList.add(suggestProductModel);
                                    suggestProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        //Get all products belongs to brand Xiaomi
        if(brand != null && brand.equalsIgnoreCase("Xiaomi")) {
            suggestProductModelList = new ArrayList<>();
            suggestProductAdapter = new SuggestProductAdapter(this, suggestProductModelList);
            rc_product.setLayoutManager(new GridLayoutManager(this,2));
            rc_product.setAdapter(suggestProductAdapter);
            toolbarNew.setTitle("Xiaomi".toUpperCase(Locale.ROOT));

            firestore.collection("AllProducts").whereEqualTo("pro_brand", "Xiaomi")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    SuggestProductModel suggestProductModel = document.toObject(SuggestProductModel.class);
                                    suggestProductModelList.add(suggestProductModel);
                                    suggestProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        //Get all products belongs to brand Amazfit
        if(brand != null && brand.equalsIgnoreCase("Amazfit")) {
            suggestProductModelList = new ArrayList<>();
            suggestProductAdapter = new SuggestProductAdapter(this, suggestProductModelList);
            rc_product.setLayoutManager(new GridLayoutManager(this,2));
            rc_product.setAdapter(suggestProductAdapter);
            toolbarNew.setTitle("Amazfit".toUpperCase(Locale.ROOT));

            firestore.collection("AllProducts").whereEqualTo("pro_brand", "Amazfit")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    SuggestProductModel suggestProductModel = document.toObject(SuggestProductModel.class);
                                    suggestProductModelList.add(suggestProductModel);
                                    suggestProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbarNew);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarNew.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}