package com.example.clininallied;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clininallied.SQLite.SQLiteHelperClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets listener for UI adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay execution to simulate loading or splash screen
        new Handler().postDelayed(this::checkUserAuthentication, 3000);
    }

    /**
     * Checks if a user is authenticated and proceeds accordingly.
     */
    private void checkUserAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fetchUserProfile(user);
        } else {
            // Fetch data first, then navigate
            handleFirestoreFetch(new OnFetchCompleteListener() {
                @Override
                public void onFetchComplete() {
                    navigateToLoginScreen(); // Move to the next screen after data is ready
                }
            });
        }
    }

    /**
     * Fetches user profile data from Firebase and updates UI or state.
     *
     * @param user Firebase authenticated user
     */
    private void fetchUserProfile(FirebaseUser user) {
        String uid = user.getUid();
        getImageProfile(uid, "users", imageUrl -> {
            UserData.setAvatar(imageUrl);
            fetchDataFromDatabase(user);
        });
    }

    /**
     * Fetches user-specific data from Firebase Realtime Database.
     *
     * @param user Firebase authenticated user
     */
    private void fetchDataFromDatabase(FirebaseUser user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                String username = snapshot.child("username").getValue(String.class);
                UserData.setEmail(email != null ? email : "");
                UserData.setUsername(username != null ? username : "");

                // Populate doctors and then navigate
                UserData.populateDoctors(MainActivity.this);
                navigateToDashboard();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: ", error.toException());
                handleFirestoreFetch(null); // If real-time DB fails, use Firestore
            }
        });
    }

    /**
     * Retrieves the profile image URL from Firebase Storage.
     *
     * @param fileName      The file name or user ID.
     * @param directoryName The directory where the image is stored.
     * @param listener      A callback listener to handle the fetched URL.
     */
    private void getImageProfile(String fileName, String directoryName, OnImageFetchedListener listener) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(directoryName + "/" + fileName);

        fileReference.getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onImageFetched(uri.toString()))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching image: ", e);
                    listener.onImageFetched(null); // Handle failure scenario
                });
    }

    /**
     * Fetches doctor data from Firestore and updates the local state.
     * Notifies the completion via the callback listener.
     */
    private void handleFirestoreFetch(OnFetchCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctors").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    String docName = snapshot.getString("docName");
                    String docSpec = snapshot.getString("docSpeciality");
                    String docCollege = snapshot.getString("docCollege");
                    String docPatients = snapshot.getString("docPatients");
                    String docExperience = snapshot.getString("docExperience");
                    String docRating = snapshot.getString("docRating");

                    StorageReference reference = FirebaseStorage.getInstance().getReference();
                    reference.child("doctors/" + docName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("DoctorImageFetcher", "Image URL: " + uri.toString());
                            Doctors doctor = new Doctors(uri.toString(), docName, docSpec, docCollege, docPatients, docRating, docExperience);
                            UserData.setDoctorData(doctor);
                            listener.onFetchComplete();




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }
        });
    }





    private void navigateToDashboard() {
        startActivity(new Intent(MainActivity.this, Dashboard.class));
        finish();
    }

    /**
     * Navigates to the Login screen.
     */
    private void navigateToLoginScreen() {
        startActivity(new Intent(MainActivity.this, ScreenLogin.class));
        finish();
    }

    /**
     * A callback interface to handle image fetch results.
     */
    public interface OnImageFetchedListener {
        void onImageFetched(String imageUrl);
    }

    /**
     * A callback interface to handle the completion of fetch operations.
     */
    public interface OnFetchCompleteListener {
        void onFetchComplete();
    }
}
