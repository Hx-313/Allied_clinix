package com.example.clininallied;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.clininallied.SQLite.SQLiteHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Profile_Fragment extends Fragment {

    private static final int GALLERY_CODE_ACTION = 1000;
    Button btn_profile;
    TextView update;
    TextView retreive;
    TextView username;
    TextView email;
    Dialog dialog;
    FirebaseDatabase db;
    DatabaseReference reference;
    ShapeableImageView avatar;
    TextView addDoc;
    Uri uri;
    SQLiteHelperClass helperClass;
    Doctors doctor = new Doctors();
    public Profile_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);
       btn_profile = view.findViewById(R.id.logout);
       email = view.findViewById(R.id.emailHolder);
        username = view.findViewById(R.id.usernameHolder);
        email.setText(UserData.getEmail());
        username.setText(UserData.getUsername());
        dialog = new Dialog(requireContext());
        update = view.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }

        });
        retreive = view.findViewById(R.id.retrieve);
        retreive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRetrieveDialog();
            }
        });
        addDoc = view.findViewById(R.id.addDoc);
        addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDoctor();
            }
        });


       btn_profile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(getContext(), "Loging "+ UserData.getUsername() + "out", Toast.LENGTH_SHORT).show();
               FirebaseAuth.getInstance().signOut();
               Intent intent = new Intent(getActivity(), ScreenLogin.class);
               startActivity(intent);
               getActivity().finish();
           }
       });

        avatar = view.findViewById(R.id.avatar_image);



      if(UserData.getAvatar() ==null){
          avatar.setImageResource(UserData.getDrawableAvatar());
      }else{
          Glide.with(this).load(UserData.getAvatar()).into(avatar);
      }

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionDialog();
            }
        });


        return view;
    }
    private void selectionDialog(){
        dialog.setContentView(R.layout.upload_options_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView local = dialog.findViewById(R.id.uploadfromlocal);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageCapture();
                dialog.dismiss();
            }
        });
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadDialog();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addDoctor() {
        dialog.setContentView(R.layout.add_doctor);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView imageView = dialog.findViewById(R.id.avatar_image);
        if (UserData.getAvatar() == null) {
            imageView.setImageResource(UserData.getDrawableAvatar());
        } else {
            Glide.with(this).load(UserData.getAvatar()).into(imageView);
        }

        EditText name = dialog.findViewById(R.id.Name);
        EditText speciality = dialog.findViewById(R.id.speciality);
        EditText college = dialog.findViewById(R.id.college);
        EditText patients = dialog.findViewById(R.id.patients);
        EditText rating = dialog.findViewById(R.id.rating);
        EditText xperience = dialog.findViewById(R.id.experience);
        Button addDoc = dialog.findViewById(R.id.add);
        Button cancel = dialog.findViewById(R.id.Go);
        ShapeableImageView avatarImage = dialog.findViewById(R.id.avatar_image);
        TextView upload = dialog.findViewById(R.id.uploadImage);
        View.OnClickListener commonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                imagePickerLauncher.launch(photoPicker);
                if (doctor.getImage() == null) {
                    Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    avatarImage.setImageURI(Uri.parse(doctor.getImage()));
                }
            }
        };
        avatarImage.setOnClickListener(commonClickListener);
        upload.setOnClickListener(commonClickListener);
        cancel.setOnClickListener(v -> dialog.dismiss());

        addDoc.setOnClickListener(v -> {
            String Name = name.getText().toString();
            String Speciality = speciality.getText().toString();
            String College = college.getText().toString();
            String Patients = patients.getText().toString();
            String Rating = rating.getText().toString();
            String Experience = xperience.getText().toString();

            if (Name.isEmpty() || Speciality.isEmpty() || College.isEmpty()
                    || Patients.isEmpty() || Rating.isEmpty() || Experience.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }else if(doctor.getImage() ==null){
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();

            }
            else {

                Map<String,Object> data = new HashMap<>();
                data.put("docName",Name);
                data.put("docSpeciality",Speciality);
                data.put("docCollege",College);
                data.put("docPatients",Patients);
                data.put("docRating",Rating);
                data.put("docExperience",Experience);

                FirebaseFirestore db = FirebaseFirestore.getInstance() ;
                db.collection("doctors").document(Name).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        UploadImage(Uri.parse(doctor.getImage()),"doctors", Name);
                        Toast.makeText(requireContext(), "Doctor added successfully", Toast.LENGTH_SHORT).show();

                        doctor.setName(Name);
                        doctor.setSpeciality(Speciality);
                        doctor.setCollege(College);
                        doctor.setPatients(Patients);
                        doctor.setExperience(Experience);
                        doctor.setRatings(Rating);
                        UserData.setDoctorData(doctor);
                        try (SQLiteHelperClass helperClass = new SQLiteHelperClass(requireContext())) {
                            helperClass.addNewDoctor(doctor , false);
                            Toast.makeText(requireContext(), "Doctor added successfully", Toast.LENGTH_SHORT).show();
                        } catch (SQLiteException e) {
                            Toast.makeText(requireContext(), "Error adding doctor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error adding doctor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        dialog.show();
    }
    ActivityResultLauncher<Intent> imagePickerLauncher= registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        uri=data.getData();
                        doctor.setImage(uri.toString());

                    }else {
                        Toast.makeText(getActivity(),"Image is not selected",Toast.LENGTH_LONG);
                    }

                }
            });
    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bundle extras = data.getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                Uri uri = saveBitmapToUri(requireContext(), imageBitmap);
                                if (uri != null) {
                                    UserData.setAvatar(uri.toString());
                                    if (UserData.getAvatar() == null) {
                                        avatar.setImageResource(UserData.getDrawableAvatar());
                                    } else {
                                        Glide.with(requireContext())
                                                .load(UserData.getAvatar())
                                                .into(avatar);
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        UploadImage(uri, "users", uid);
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Failed to save image", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "No image data found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "No data received from camera", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Image capture failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );
    private Uri saveBitmapToUri(Context context, Bitmap bitmap) {
        File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Compress to JPEG format
            fos.flush();
            fos.close();

            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", imageFile);
        } catch (IOException e) {
            Log.e("ImageSave", "Failed to save image to file", e);
            return null;
        }
    }
    ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        uri=data.getData();
                        UserData.setAvatar(uri.toString());


                        if(UserData.getAvatar() ==null){
                            avatar.setImageResource(UserData.getDrawableAvatar());
                        }else{
                            avatar.setImageURI(Uri.parse(UserData.getAvatar()));
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            UploadImage(uri,"users",uid);
                        }

                    }else {
                        Toast.makeText(getActivity(),"Image is not selected",Toast.LENGTH_LONG);
                    }

                }
            });

    private void showImageCapture() {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(iCamera);

    }

    private void showUploadDialog() {
        dialog.setContentView(R.layout.upload_image_diaog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
       ImageView imageView = dialog.findViewById(R.id.avatar_image);
        if(UserData.getAvatar() ==null){
            imageView.setImageResource(UserData.getDrawableAvatar());
        }else{
            Glide.with(this).load(UserData.getAvatar()).into(imageView);
        }


        Button view = dialog.findViewById(R.id.view);
        Button upload  = dialog.findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker = new Intent(Intent.ACTION_PICK);
                photopicker.setType("image/*");
                activityResultLauncher.launch(photopicker);

                if(UserData.getAvatar() ==null){
                    imageView.setImageResource(UserData.getDrawableAvatar());
                }else{
                    imageView.setImageURI(Uri.parse(UserData.getAvatar()));
                }
                avatar = getView().findViewById(R.id.avatar_image);
                if(UserData.getAvatar() ==null){
                    avatar.setImageResource(UserData.getDrawableAvatar());
                }else{
                    avatar.setImageURI(Uri.parse(UserData.getAvatar()));
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.view_profile_image);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                ImageView avatar = dialog.findViewById(R.id.profile_view);

                if(UserData.getAvatar() ==null){
                    avatar.setImageResource(UserData.getDrawableAvatar());
                }else{
                    avatar.setImageURI(Uri.parse(UserData.getAvatar()));
                }
                dialog.show();
            }
        });





        // Show the dialog
        dialog.show();
    }

    private void showUpdateDialog() {
        dialog.setContentView(R.layout.update_profile);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView email = dialog.findViewById(R.id.emailHolder);
        email.setText(UserData.getEmail());
        Button dialogUpdateButton = dialog.findViewById(R.id.update);
        avatar = dialog.findViewById(R.id.avatar_image);

        if(UserData.getAvatar() ==null){
            avatar.setImageResource(UserData.getDrawableAvatar());
        }else{
            Glide.with(this).load(UserData.getAvatar()).into(avatar);
        }
        if (dialogUpdateButton != null) {
            dialogUpdateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText username = dialog.findViewById(R.id.username);
                    String newUsername = username.getText().toString();
                    if(newUsername.isEmpty()){
                        username.setError("Username cannot be empty");
                        return;
                    }
                    updateData(newUsername);
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Dialog update button not found!", Toast.LENGTH_SHORT).show();
        }
        Button dialogCancelButton = dialog.findViewById(R.id.Go);
        if (dialogCancelButton != null) {
            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Dialog cancel button not found!", Toast.LENGTH_SHORT).show();
        }

        // Show the dialog
        dialog.show();
    }
    private void showRetrieveDialog() {
        try {
            dialog.setContentView(R.layout.retrieve_user);


            // Set the dialog to match the parent width and wrap content height
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.setCancelable(false);

            // Find and set the username TextView
            TextView username = dialog.findViewById(R.id.usernameHolder);
            if (username != null) {
                username.setText(UserData.getUsername());
            } else {
                Toast.makeText(requireContext(), "Username TextView not found!", Toast.LENGTH_SHORT).show();
            }
            avatar = dialog.findViewById(R.id.avatar_image);

            if(UserData.getAvatar() ==null){
                avatar.setImageResource(UserData.getDrawableAvatar());
            }else{
                Glide.with(this).load(UserData.getAvatar()).into(avatar);
            }

            // Find and set the cancel button
            Button dialogCancelButton = dialog.findViewById(R.id.Go);
            if (dialogCancelButton != null) {
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Dialog cancel button not found!", Toast.LENGTH_SHORT).show();
            }

            // Find and set the retrieve button
            Button dialogRetrieveButton = dialog.findViewById(R.id.retrieve);
            if (dialogRetrieveButton != null) {
                dialogRetrieveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText username = dialog.findViewById(R.id.username);
                        String newUsername = username.getText().toString();
                        if(newUsername.isEmpty()){
                            username.setError("Username cannot be empty");
                            return;
                        }
                        db = FirebaseDatabase.getInstance();
                        reference = db.getReference("users");
                        reference.orderByChild("username").equalTo(newUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Loop through all users with the matching username
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        // Retrieve the email and username
                                        String email = userSnapshot.child("email").getValue(String.class);
                                        String username = userSnapshot.child("username").getValue(String.class);

                                        // Update your dialog's TextViews
                                        TextView emailHolder = dialog.findViewById(R.id.email);
                                        emailHolder.setText("Email: " + email);
                                        emailHolder.setVisibility(View.VISIBLE);

                                        TextView usernameHolder = dialog.findViewById(R.id.name);
                                        usernameHolder.setText("Username: " + username);
                                        usernameHolder.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(requireContext(), "Error retrieving data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                });
            } else {
                Toast.makeText(requireContext(), "Dialog retrieve button not found!", Toast.LENGTH_SHORT).show();
            }

            // Show the dialog
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error showing dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void updateUsernameInFragment(String newUsername) {

        username = getView().findViewById(R.id.usernameHolder);
        if (username != null) {
            username.setText(newUsername);
        }
    }
    private void updateData(String newUsername){
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("users");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child(uid).child("username").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                    UserData.setUsername(newUsername);
                    updateUsernameInFragment(newUsername);
                    dialog.dismiss();
                }else{
                    Toast.makeText(requireContext(), "Failed to update username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void UploadImage(Uri uri,String foldeName,String fileName) {

        if (fileName == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileReference = storageReference.child(foldeName+"/" + fileName);

        fileReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e -> {
                    Log.e("UploadImage", "Failed to upload image: ", e);
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




}