package com.example.clininallied;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReference("users");
                    Toast.makeText(MainActivity.this, "Welcome " + user.getUid(), Toast.LENGTH_SHORT).show();
                    // Use user.getUid() instead of email or sanitize the email properly
                    reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String email = snapshot.child("email").getValue(String.class);
                            String username = snapshot.child("username").getValue(String.class);
                            UserData.setEmail(email);
                            UserData.setUsername(username);
                            if (UserData.getEmail() != null) {
                                // User data exists, navigate to the dashboard
                                startActivity(new Intent(MainActivity.this, Dashboard.class));
                            } else {
                                // User data doesn't exist, navigate to the login screen
                                startActivity(new Intent(MainActivity.this, ScreenLogin.class));
                            }
                            finish(); // Close the current activity
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error if the database operation is cancelled
                            startActivity(new Intent(MainActivity.this, ScreenLogin.class));
                            finish(); // Close the current activity
                        }
                    });
                } else {
                    // User is not logged in, navigate to the login screen
                    startActivity(new Intent(MainActivity.this, ScreenLogin.class));
                    finish(); // Close the current activity
                }
            }
        }, 3000);
    }
}