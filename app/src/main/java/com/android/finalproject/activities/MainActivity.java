package com.android.finalproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.finalproject.R;
import com.android.finalproject.fragments.HomeFragment;
import com.android.finalproject.fragments.ProductFragment;
import com.android.finalproject.fragments.ProfileFragment;
import com.android.finalproject.fragments.SupportFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    LinearLayout home, products, support, profile;
    FloatingActionButton cart;
    Fragment homeFragment, supportFragment, profileFragment, productsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment =  new HomeFragment();
        loadFragment(homeFragment);

        initView();
        initControlFragmentView();

    }

    private void initControlFragmentView() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment =  new HomeFragment();
                loadFragment(homeFragment);
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productsFragment = new ProductFragment();
                loadFragment(productsFragment);
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFragment = new SupportFragment();
                loadFragment(supportFragment);
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragment = new ProfileFragment();
                loadFragment(profileFragment);
            }
        });
    }

    private void initView() {
        home = findViewById(R.id.btnHome);
        products = findViewById(R.id.btnProducts);
        support = findViewById(R.id.btnSupport);
        profile = findViewById(R.id.btnProfile);
        cart = findViewById(R.id.cart);

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, fragment);
        transaction.commit();
    }
}