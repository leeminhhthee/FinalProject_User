package com.android.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.adapters.AddressAdapter;
import com.android.finalproject.models.AddressModel;
import com.android.finalproject.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress{
    Button addAddressBtn;
    AppCompatButton btnPayment;
    Toolbar toolbar;
    int total;
    TextView tvTotalCheck, name, email, phone;
    RecyclerView rc_address;
    String mAddress = "";

    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initView();
        ActionToolbar();

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        total = getIntent().getIntExtra("total",0);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tvTotalCheck.setText(decimalFormat.format(total) + " VND");
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.address_toolbar);
        addAddressBtn = findViewById(R.id.add_address_btn);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this, AddAddressActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        tvTotalCheck = findViewById(R.id.tvTotalCheck);
        addressModelList = new ArrayList<>();
        rc_address = findViewById(R.id.address_recycler);
        rc_address.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressAdapter = new AddressAdapter(getApplicationContext(), addressModelList,this);
        rc_address.setAdapter(addressAdapter);

        name = findViewById(R.id.tvNameCheckout);
        name.setText(auth.getCurrentUser().getDisplayName());

        email = findViewById(R.id.tvEmailCheckout);
        email.setText(auth.getCurrentUser().getEmail());

        phone = findViewById(R.id.tvPhoneCheckout);
        final String[] mobile_phone = {""};
        String mobile;
        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Phone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                mobile_phone[0] = doc.getString("userPhone");
                            }
                            phone.setText(mobile_phone[0]);
                        }
                    }
                });

        btnPayment = findViewById(R.id.payment_btn);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderName = name.getText().toString();
                String orderPhone = phone.getText().toString();
                String orderEmail = email.getText().toString();
                String orderAddress = mAddress;
                int orderTotal = total;

                String saveCurrentDate;
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM yyyy");
                saveCurrentDate = currentDate.format(calForDate.getTime());

                final String docId;
                //Put user information order to database
                final HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("name", orderName);
                cartMap.put("phone", orderPhone);
                cartMap.put("email", orderEmail);
                cartMap.put("address", orderAddress);
                cartMap.put("total", orderTotal);
                cartMap.put("date", saveCurrentDate);
                cartMap.put("status", "Dang chuan bi hang!");

                docId = firestore.collection("Orders").document(auth.getCurrentUser().getUid())
                        .collection("Orderdetail").document().getId();

                firestore.collection("Orders").document(auth.getCurrentUser().getUid())
                        .collection("Orderdetail").document(docId).collection("information").document("receiver")
                        .set(cartMap);

                //Get product from cart
                //then, put product order to database
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                        MyCartModel myCartModel = doc.toObject(MyCartModel.class);

                                        final HashMap<String, Object> proMap = new HashMap<>();
                                        proMap.put("proName", myCartModel.getProductName());
                                        proMap.put("proPrice", myCartModel.getProductPrice());
                                        proMap.put("proQty", myCartModel.getTotalQty());
                                        proMap.put("proImg", myCartModel.getProductImg());
                                        proMap.put("totalPrice", myCartModel.getTotalPrice());

                                        firestore.collection("Orders").document(auth.getCurrentUser().getUid())
                                                .collection("Orderdetail").document(docId).collection("products")
                                                .add(proMap);
                                    }
                                }
                            }
                        });

                //Delete cart
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                        doc.getReference().delete();
                                    }
                                }
                            }
                        });

                Toast.makeText(CheckoutActivity.this, "Ordered successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void setAddress(String address) {
        mAddress = address;
    }
}