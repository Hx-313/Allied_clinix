package com.example.clininallied;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;


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
    Uri uri;
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
                showUploadDialog();
            }
        });


        return view;
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
                            UploadImage(uri);
                        }

                    }else {
                        Toast.makeText(getActivity(),"Image is not selected",Toast.LENGTH_LONG);
                    }

                }
            });

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
    private void UploadImage(Uri uri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileReference = storageReference.child("users/" + uid);

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