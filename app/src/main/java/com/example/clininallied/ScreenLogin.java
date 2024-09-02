package com.example.clininallied;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clininallied.databinding.ActivityScreenLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ScreenLogin extends AppCompatActivity {
    ActivityScreenLoginBinding binding;

    String email;
    String passswrd;
    String Username;
    FirebaseDatabase db;
    FirebaseAuth auth;
    DatabaseReference reference;

    boolean isVisible = false;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_login);
        EdgeToEdge.enable(this);
        binding  = ActivityScreenLoginBinding.inflate(getLayoutInflater());
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
        binding.forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScreenLogin.this,ForgotPassword.class));
            }
        });

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = binding.email.getText().toString();
                 passswrd = binding.password.getText().toString();
                 if(email.isEmpty() || passswrd.isEmpty()){
                     binding.email.setError("Please enter Email");
                     binding.password.setError("Please enter Password");
                     return;
                 }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.email.setError("Enter Valid Email");
                    return;
                }
                     auth = FirebaseAuth.getInstance();
                     auth.signInWithEmailAndPassword(email,passswrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               String userId = auth.getCurrentUser().getUid();
                               StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                               StorageReference fileReference = storageReference.child( "users/" + userId);

                               fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       UserData.setAvatar(uri.toString());
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(ScreenLogin.this, "Log In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                   }
                               });
                               db = FirebaseDatabase.getInstance();
                               reference = db.getReference("users");
                               reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {


                                       String email = snapshot.child("email").getValue(String.class);
                                       String username = snapshot.child("username").getValue(String.class);
                                       UserData.setEmail(email);
                                       UserData.setUsername(username);
                                       startActivity(new Intent(ScreenLogin.this,Dashboard.class));

                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       Toast.makeText(ScreenLogin.this, "Log In failed: " + error.getMessage(), Toast.LENGTH_LONG).show();

                                   }
                               });

                           }else{
                               Toast.makeText(ScreenLogin.this, "Log In failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                           }
                         }
                     });

            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenLogin.this, RegistrationScreen.class);
                startActivity(intent);


            }
        });

    }
    private void getImageProfile(String fileName, String directoryName, MainActivity.OnImageFetchedListener listener) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileReference = storageReference.child(directoryName + "/" + fileName);

        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                listener.onImageFetched(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onImageFetched(null); // Handle failure scenario
            }
        });
    }
    public interface OnImageFetchedListener {
        void onImageFetched(String imageUrl);
    }

}