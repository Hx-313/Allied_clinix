package com.example.clininallied;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class RegistrationScreen extends AppCompatActivity {
    DrawableEditText password;
    boolean isVisible = false;
    EditText username;
    EditText email;
    CheckBox agree;
    Button signup;
    FirebaseAuth auth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        password = findViewById(R.id.password);
        password.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                if(!isVisible) {

                    isVisible=!isVisible;
                    password.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_password_svgrepo_com, 0 ,R.drawable.eye, 0);
                }else{
                    isVisible= !isVisible;
                    password.setInputType( InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_password_svgrepo_com, 0 ,R.drawable.hide, 0);

                }
            }
        });
        signup = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        agree = findViewById(R.id.agree);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name  = username.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                if(name.isEmpty() || mail.isEmpty() || pass.isEmpty()){
                    username.setError("Please enter Username");
                    email.setError("Please enter Email");
                    password.setError("Please enter Password");
                    return;
                }
                if(!agree.isChecked()){
                    agree.setError("Please agree to the terms and conditions");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword( mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(RegistrationScreen.this, LoginScreen.class));

//                            FirebaseUser user = auth.getCurrentUser();
//                            if (user != null) {
//
//                                String userId = user.getUid();
//
//
//                                // Save user data to Firestore
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                Map<String, Object> userData = new HashMap<>();
//                                userData.put("username", name);
//                                userData.put("email", mail);


//                                db.collection("users").document(userId)
//                                        .set(userData)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    // Proceed to login screen after saving user data
//                                                    finish();
//                                                } else {
//                                                    Toast.makeText(RegistrationScreen.this, "Failed to save user data.", Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        });
                            //}
                        } else {
                            // If registration fails, display a message to the user.
                            Toast.makeText(RegistrationScreen.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }                   }
                });

            }
        });
    }
}