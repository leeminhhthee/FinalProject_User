package com.android.finalproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.models.MyCartModel;
import com.android.finalproject.models.NewProductModel;
import com.android.finalproject.models.ShowAllModel;
import com.android.finalproject.models.SuggestProductModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    FrameLayout frameCart;
    TextView productName, productPrice, productDes, productQty;
    AppCompatButton btnAddCart;
    ImageView productImg, btnIncrease, btnDecrease;
    Button btnBack;
    Rating productRatingDetail;

    int totalQty = 1;
    int totalPrice = 0;
    int proPrice;
    String proImg, proId;
    int qtyEnd = 0, priceEnd = 0;

    //New products
    NewProductModel newProductModel = null;
    //Suggest products
    SuggestProductModel suggestProductModel = null;
    //Show all products
    ShowAllModel showAllModel = null;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");
        if(obj instanceof NewProductModel){
            newProductModel = (NewProductModel) obj;
        } else if(obj instanceof SuggestProductModel){
            suggestProductModel = (SuggestProductModel) obj;
        } else if(obj instanceof ShowAllModel){
            showAllModel = (ShowAllModel) obj;
        }

        initView();
        initControl();
    }

    private void initControl() {
        //New products
        if(newProductModel != null){
            Glide.with(getApplicationContext()).load(newProductModel.getImg_url()).into(productImg);
            productName.setText(newProductModel.getName());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            productPrice.setText(decimalFormat.format(newProductModel.getPrice()) + " VND");
            productDes.setText(newProductModel.getDescription());

            totalPrice = newProductModel.getPrice() * totalQty;
            proPrice = newProductModel.getPrice();
            proImg = newProductModel.getImg_url();
            proId = newProductModel.getId();
        }

        //Suggest products
        if(suggestProductModel != null){
            Glide.with(getApplicationContext()).load(suggestProductModel.getImg_url()).into(productImg);
            productName.setText(suggestProductModel.getName());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            productPrice.setText(decimalFormat.format(suggestProductModel.getPrice()) + " VND");
            productDes.setText(suggestProductModel.getDescription());

            totalPrice = suggestProductModel.getPrice() * totalQty;
            proPrice = suggestProductModel.getPrice();
            proImg = suggestProductModel.getImg_url();
            proId = suggestProductModel.getId();
        }

        //Show all products
        if(showAllModel != null){
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(productImg);
            productName.setText(showAllModel.getName());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            productPrice.setText(decimalFormat.format(showAllModel.getPrice()) + " VND");
            productDes.setText(showAllModel.getDescription());

            totalPrice = showAllModel.getPrice() * totalQty;
            proPrice = showAllModel.getPrice();
            proImg = showAllModel.getImg_url();
            proId = showAllModel.getId();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });

        //Add to cart
        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        //Increase quantity item
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQty < 10){
                    totalQty++;
                    productQty.setText(String.valueOf(totalQty));

                    if(newProductModel != null){
                        totalPrice = newProductModel.getPrice()* totalQty;
                    }
                    if(suggestProductModel != null){
                        totalPrice = suggestProductModel.getPrice()* totalQty;
                    }
                    if(showAllModel != null){
                        totalPrice = showAllModel.getPrice()* totalQty;
                    }
                }
            }
        });
        //Decrease quantity item
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQty > 1 ){
                    totalQty--;
                    productQty.setText(String.valueOf(totalQty));
                }
            }
        });

        //Open cart
        frameCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });
    }

    private void addToCart() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                if(myCartModel.getProductName().equals(productName.getText().toString())){
                                    qtyEnd = myCartModel.getTotalQty();
                                    priceEnd = myCartModel.getTotalPrice();
                                    doc.getReference().delete();
                                }
                            }
                            //Add new data
                            final HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("productName", productName.getText().toString());
                            cartMap.put("productImg", proImg);
                            cartMap.put("productPrice", proPrice);
                            cartMap.put("totalQty", Integer.parseInt(productQty.getText().toString()) + qtyEnd);
                            cartMap.put("totalPrice", totalPrice + priceEnd);
                            cartMap.put("currentTime", saveCurrentTime);
                            cartMap.put("currentDate", saveCurrentDate);
                            cartMap.put("productId", proId);

                            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                    .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Toast.makeText(DetailActivity.this, "Successfully! Added To A Cart.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

    }

    private void initView() {
        productName = findViewById(R.id.tvProductNameDetail);
        productPrice = findViewById(R.id.tvProductPriceDetail);
        productDes = findViewById(R.id.tvProductDesDetail);
        productQty = findViewById(R.id.tvProductQtyDetail);
        btnAddCart = findViewById(R.id.btnAddCartDetail);
        productImg = findViewById(R.id.productImgDetail);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnBack = findViewById(R.id.btnBack);
        frameCart = findViewById(R.id.frameCart);
    }
}