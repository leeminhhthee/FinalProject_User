package com.android.finalproject.adapters;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.finalproject.fragments.Products.AmazfitFragment;
import com.android.finalproject.fragments.Products.AppleFragment;
import com.android.finalproject.fragments.Products.SamsungFragment;
import com.android.finalproject.fragments.Products.XiaomiFragment;
import com.android.finalproject.models.BrandModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductViewPagerAdapter extends FragmentStatePagerAdapter {

    public ProductViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AppleFragment();
            case 1:
                return new XiaomiFragment();
            case 2:
                return new SamsungFragment();
            case 3:
                return new AmazfitFragment();
            default:
                return new AppleFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Apple";
            case 1:
                return "Xiaomi";
            case 2:
                return "Samsung";
            case 3:
                return "Amazfit";
            default:
                return "Apple";
        }
    }
}
