package com.android.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.adapters.AddressAdapter;
import com.android.finalproject.models.AddressModel;
import com.android.finalproject.models.CreateOrder;
import com.android.finalproject.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CheckoutActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress{
    Button addAddressBtn;
    AppCompatButton btnPayment, btnZaloPay;
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

        //Zalo Pay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        //Get address
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

        //Payment delivery
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

                docId = firestore.collection("orders").document().getId();
                //Put user information order to database
                final HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("name", orderName);
                cartMap.put("phone", orderPhone);
                cartMap.put("email", orderEmail);
                cartMap.put("address", orderAddress);
                cartMap.put("total", orderTotal);
                cartMap.put("date", saveCurrentDate);
                cartMap.put("status", "Đơn hàng đang được xử lí");
                cartMap.put("id", docId);


                firestore.collection("orders").document(docId).set(cartMap);

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

                                        firestore.collection("orders").document(docId).collection("order_detail")
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
                //Post thông báo cho admin
                /* Map<String, String> data = new HashMap<>();
                data.put("title", "Đơn hàng mới");
                data.put("message", "Bạn có một đơn hàng mới cần xử lý");

                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder("admin_app")
                        .setData(data)
                        .build()); */


                Toast.makeText(CheckoutActivity.this, "Ordered successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
                finish();
            }
        });

        //Zalo Pay
        btnZaloPay = findViewById(R.id.payment_zalopay);
        btnZaloPay.setOnClickListener(new View.OnClickListener() {
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

                // Tạo đối tượng CreateOrder để gọi API tạo đơn hàng
                CreateOrder orderApi = new CreateOrder();
                try {
                    // Tạo đơn hàng với giá trị amount
                    JSONObject data = orderApi.createOrder(String.valueOf(total));
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token = ((JSONObject) data).getString("zp_trans_token");
                        // Gọi ZaloPay SDK để thực hiện thanh toán
                        ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, "demozpdk://app", new PayOrderListener() {

                            @Override
                            public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                                final String docId;

                                docId = firestore.collection("orders").document().getId();
                                //Put user information order to database
                                final HashMap<String, Object> cartMap = new HashMap<>();
                                cartMap.put("name", orderName);
                                cartMap.put("phone", orderPhone);
                                cartMap.put("email", orderEmail);
                                cartMap.put("address", orderAddress);
                                cartMap.put("total", 0);
                                cartMap.put("date", saveCurrentDate);
                                cartMap.put("status", "Đơn hàng đã được thanh toán");
                                cartMap.put("id", docId);


                                firestore.collection("orders").document(docId).set(cartMap);

                                //Get product from cart, put product order to database
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

                                                        firestore.collection("orders").document(docId).collection("order_detail")
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

                            @Override
                            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(CheckoutActivity.this)
                                        .setTitle("User Cancel Payment")
                                        .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).show();
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                new AlertDialog.Builder(CheckoutActivity.this)
                                        .setTitle("Payment Fail")
                                        .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), s))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setAddress(String address) {
        mAddress = address;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}