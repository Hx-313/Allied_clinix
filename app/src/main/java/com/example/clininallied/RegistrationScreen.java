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

import com.example.clininallied.databinding.ActivityRegistrationScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationScreen extends AppCompatActivity {

    boolean isVisible = false;
    String username,email,password;
   ActivityRegistrationScreenBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_screen);
        binding = ActivityRegistrationScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.password.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                if(!isVisible) {

                    isVisible=!isVisible;
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_password_svgrepo_com, 0 ,R.drawable.eye, 0);
                }else{
                    isVisible= !isVisible;
                    binding.password.setInputType( InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_password_svgrepo_com, 0 ,R.drawable.hide, 0);

                }
            }
        });

        auth = FirebaseAuth.getInstance();

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 username  = binding.username.getText().toString();
                 email = binding.email.getText().toString();
                 password = binding.password.getText().toString();
                if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                    binding.username.setError("Please enter Username");
                    binding.email.setError("Please enter Email");
                    binding.password.setError("Please enter Password");
                    return;
                }
                if(!binding.agree.isChecked()){
                    binding.agreement.setError("Please agree to the terms and conditions");
                    return;
                }
                binding.agreement.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword( email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                               UserData.setUsername(username);
                               UserData.setEmail(email);
                               UserData.setPasssword(password);
                            Map<String, Object> userDataMap = new HashMap<>();
                            userDataMap.put("username", UserData.getUsername());  // Assuming UserData has static getters
                            userDataMap.put("email", UserData.getEmail());
                            userDataMap.put("password", UserData.getPasssword());

                                db = FirebaseDatabase.getInstance();
                                reference = db.getReference("users");
                                reference.child(auth.getUid().toString()).setValue(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                      if(task.isSuccessful()){
                                          binding.username.setText("");
                                          binding.password.setText("");
                                          binding.email.setText("");
                                          Toast.makeText(getApplicationContext(),"Sign Up successfull",Toast.LENGTH_SHORT).show();
                                          startActivity(new Intent(RegistrationScreen.this, ScreenLogin.class));

                                      }
                                      else {
                                          Toast.makeText(RegistrationScreen.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                      }
                                    }
                                });


                        } else {
                            // If registration fails, display a message to the user.
                            Toast.makeText(RegistrationScreen.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }                   }
                });

            }
        });
    }
}