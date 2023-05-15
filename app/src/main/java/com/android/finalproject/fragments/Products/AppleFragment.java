package com.android.finalproject.fragments.Products;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.adapters.NewProductAdapter;
import com.android.finalproject.adapters.ShowAllAdapter;
import com.android.finalproject.adapters.SuggestProductAdapter;
import com.android.finalproject.models.NewProductModel;
import com.android.finalproject.models.ShowAllModel;
import com.android.finalproject.models.SuggestProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppleFragment extends Fragment {

    private RecyclerView rc_apple;
    View view;
    //NewProduct recyclerview
    SuggestProductAdapter adapter;
    List<SuggestProductModel> list;

    //FirebaseStore
    FirebaseFirestore db;

    public AppleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_products_apple, container, false);

        rc_apple = view.findViewById(R.id.rc_apple);
        rc_apple.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        list = new ArrayList<>();
        adapter = new SuggestProductAdapter(getContext(), list);

        db = FirebaseFirestore.getInstance();
        //New products home
        db.collection("AllProducts").whereEqualTo("pro_brand", "Apple")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SuggestProductModel model = document.toObject(SuggestProductModel.class);
                                list.add(model);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        rc_apple.setAdapter(adapter);

        return view;
    }
}