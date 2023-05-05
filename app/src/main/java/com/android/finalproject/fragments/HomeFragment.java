package com.android.finalproject.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.activities.LoginActivity;
import com.android.finalproject.activities.ShowAllActivity;
import com.android.finalproject.adapters.BrandAdapter;
import com.android.finalproject.models.BrandModel;
import com.android.finalproject.adapters.NewProductAdapter;
import com.android.finalproject.models.NewProductModel;
import com.android.finalproject.adapters.SuggestProductAdapter;
import com.android.finalproject.models.SuggestProductModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    View root;

    ProgressDialog progressDialog;
    RecyclerView rc_brand, rc_new, rc_suggest;
    ConstraintLayout home_layout;
    ImageView btnLogout, userImgHome;
    TextView tvSeeAll, userNameHome;

    //Brand home recyclerview
    BrandAdapter brandAdapter;
    List<BrandModel> brandModelList;

    //NewProduct recyclerview
    NewProductAdapter newProductAdapter;
    List<NewProductModel> newProductModelList;

    //Suggest recyclerview
    SuggestProductAdapter suggestProductAdapter;
    List<SuggestProductModel> suggestProductModelList;

    //FirebaseStore
    FirebaseFirestore db;
    FirebaseUser user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        initView();
        initControl();

        //Show progress dialog
        progressDialog.setTitle("Welcome to LMT Smartwatch shop!");
        progressDialog.setMessage("Please wait a minutes...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Brand home
        db.collection("brand")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BrandModel brandModel = document.toObject(BrandModel.class);
                                brandModelList.add(brandModel);
                                brandAdapter.notifyDataSetChanged();
                                home_layout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //New products home
        db.collection("NewProducts").limit(Long.parseLong("4"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewProductModel newProductModel = document.toObject(NewProductModel.class);
                                newProductModelList.add(newProductModel);
                                newProductAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Suggest products home
        db.collection("AllProducts").limit(Long.parseLong("6"))
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
                            Toast.makeText(getContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return root;
    }

    public void initView(){
        progressDialog = new ProgressDialog(getActivity());
        rc_brand = root.findViewById(R.id.rc_brand);
        rc_new = root.findViewById(R.id.rc_new);
        rc_suggest = root.findViewById(R.id.rc_suggest);
        btnLogout = root.findViewById(R.id.btnLogout);

        //Set data of user in home fragment
        userNameHome = root.findViewById(R.id.userNameHome);
        userNameHome.setText(user.getDisplayName() + "  ");
        userImgHome = root.findViewById(R.id.userImgHome);
        if(user.getPhotoUrl() != null){
            Glide.with(getContext()).load(user.getPhotoUrl()).into(userImgHome);
        }

        //Invisible home
        home_layout = root.findViewById(R.id.home_layout);
        home_layout.setVisibility(View.GONE);

        //Brand home
        rc_brand.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rc_brand.setHasFixedSize(true);
        brandModelList = new ArrayList<>();
        brandAdapter = new BrandAdapter(getContext(), brandModelList);
        rc_brand.setAdapter(brandAdapter);

        //New products home
        rc_new.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newProductModelList = new ArrayList<>();
        newProductAdapter = new NewProductAdapter(getContext(), newProductModelList);
        rc_new.setAdapter(newProductAdapter);

        //Suggest products home
        rc_suggest.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        suggestProductModelList = new ArrayList<>();
        suggestProductAdapter = new SuggestProductAdapter(getContext(), suggestProductModelList);
        rc_suggest.setAdapter(suggestProductAdapter);

        //See all
        tvSeeAll = root.findViewById(R.id.tvSeeAll);
    };

    private void initControl() {
        //Button Log out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               logout(view);
            }
        });

        //Sell all
        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
        builder.setTitle("Warning: ");
        builder.setMessage("Are you sure? If you proceed you will be logged out immediately?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}