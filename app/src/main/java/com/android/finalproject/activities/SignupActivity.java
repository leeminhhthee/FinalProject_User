package com.android.finalproject.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.finalproject.R;
import com.android.finalproject.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    TextView tvLogin;
    TextInputEditText etEmailSignUp, etNameSignUp, etPhoneSignUp, etPasswordSignUp, etRePasswordSignUp;
    AppCompatButton btnSignUp;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
        initControl();
    }

    private void initView() {
        tvLogin = findViewById(R.id.tvLogin);
        etEmailSignUp = findViewById(R.id.etEmailSignUp);
        etNameSignUp = findViewById(R.id.etNameSignUp);
        etPhoneSignUp = findViewById(R.id.etPhoneSignUp);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etRePasswordSignUp = findViewById(R.id.etRePasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();
//        if(auth.getCurrentUser() != null){
//            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    private void initControl() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        String str_email = etEmailSignUp.getText().toString();
        String str_password = etPasswordSignUp.getText().toString();
        String str_repassword = etRePasswordSignUp.getText().toString();
        String str_username = etNameSignUp.getText().toString();
        String str_mobile = etPhoneSignUp.getText().toString();

        if(TextUtils.isEmpty(str_email)){
            Toast.makeText(this, "You have not entered your email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(str_password)){
            Toast.makeText(this, "You have not entered your password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(str_password.length() < 6){
            Toast.makeText(this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(str_repassword)){
            Toast.makeText(this, "You have not entered your rePassword.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(str_username)){
            Toast.makeText(this, "You have not entered your name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(str_mobile)){
            Toast.makeText(this, "You have not entered your phone.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(str_password.equals(str_repassword)){
            //post data
            auth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        //Add Username
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(str_username)
                                .build();
                        auth.getCurrentUser().updateProfile(profileUpdates);

                        //Add mobile phone
                        firestore = FirebaseFirestore.getInstance();
                        Map<String, String> map = new HashMap<>();
                        map.put("userPhone", str_mobile);
                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("Phone").document("Mobile_phone").set(map);

                        //Set role: User
                        Map<String, String> map2 = new HashMap<>();
                        map2.put("role", "user");
                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("Role").document("role_type").set(map2);

                        if (auth.getCurrentUser() != null) {
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Successfully Sign Up. Please check your email to verify your account.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                        if (auth.getCurrentUser().isEmailVerified()) {
                            //Successfully
                            Toast.makeText(SignupActivity.this, "Successfully Sign Up", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(SignupActivity.this, "Sign Up Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Password do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}