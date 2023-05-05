package com.android.finalproject.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.models.AddressModel;
import com.android.finalproject.models.MyCartModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {
    AppCompatButton editProfile;
    TextView userEmail, userName, userMobile, userAddress;
    ImageView userImage;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    BottomSheetDialog dialog;

    String name, email;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        userEmail = root.findViewById(R.id.userEmail);
        userName = root.findViewById(R.id.userName);
        userImage = root.findViewById(R.id.userImage);
        userMobile = root.findViewById(R.id.tvMobile);
        userAddress = root.findViewById(R.id.tvAddress);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        //Set data into CardView Profile
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }
        if(user.getPhotoUrl() != null){
            Glide.with(getContext()).load(user.getPhotoUrl()).into(userImage);
        }
        final String[] mobile_phone = {""};
        String mobile;
        firestore.collection("CurrentUser").document(user.getUid())
                .collection("Phone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                mobile_phone[0] = doc.getString("userPhone");
                            }
                            userMobile.setText(mobile_phone[0]);
                        }
                    }
                });
        firestore.collection("CurrentUser").document(user.getUid())
                .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int qtyAddress = 0;
                        if (task.isSuccessful()) {
                            qtyAddress = task.getResult().getDocuments().size();
                        }
                        userAddress.setText(qtyAddress + " Address");
                    }
                });

        //Set onClick button Edit
        editProfile = root.findViewById(R.id.btnEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_layout, null);

                TextView name = sheetView.findViewById(R.id.userNameEdit);
                TextView phone = sheetView.findViewById(R.id.userPhoneEdit);
                TextView img = sheetView.findViewById(R.id.userImgUrlEdit);

                //Set data into form EditProfile
                name.setText(user.getDisplayName());
                if(user.getPhotoUrl() != null ){
                    img.setText(user.getPhotoUrl().toString());
                }
                phone.setText(userMobile.getText());

                sheetView.findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String userName = name.getText().toString();
                        String userPhone = phone.getText().toString();
                        String userImg = img.getText().toString();

                        //Update Name and Image URL User
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .setPhotoUri(Uri.parse(userImg))
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Edited your profile", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //Update phone
                        DocumentReference phoneUpdate = firestore.collection("CurrentUser")
                                .document(user.getUid()).collection("Phone").document("Mobile_phone");
                        phoneUpdate.update("userPhone", userPhone);

                        dialog.dismiss();
                    }
                });
                dialog.setContentView(sheetView);
                dialog.show();
            }
        });

        return root;
    }
}