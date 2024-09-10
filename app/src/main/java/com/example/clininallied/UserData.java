package com.example.clininallied;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.clininallied.SQLite.DoctorsHelperParams;
import com.example.clininallied.SQLite.SQLiteHelperClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserData {
     static String email;
     static String passsword;
    static String Username;
    static String Avatar = null;
    static int drawableAvatar = R.drawable.defaualt_profile;
    static List<Doctors> doctorData = new ArrayList<>();

    public static List<Doctors> getDoctorData() {
        return doctorData;
    }

    public static void populateDoctors(Context context) {
        SQLiteHelperClass db = new SQLiteHelperClass(context);
        Cursor res = null;
        List<Doctors> doctorData = new ArrayList<>(); // Initialize doctorData list

        try {
            res = db.getData();

            if (res == null) {
                Log.e("populateDoctors", "Cursor is null");
                Toast.makeText(context, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show();
                return;
            }

            if (res.getCount() == 0) {
                Log.i("populateDoctors", "No data found in the database");
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get column indices by column names
            int nameIndex = res.getColumnIndex(DoctorsHelperParams.KEY_NAME);
            int specialityIndex = res.getColumnIndex(DoctorsHelperParams.KEY_SPECIALITY);
            int collegeIndex = res.getColumnIndex(DoctorsHelperParams.KEY_COLLEGE);
            int experienceIndex = res.getColumnIndex(DoctorsHelperParams.KEY_EXPERIENCE);
            int patientsIndex = res.getColumnIndex(DoctorsHelperParams.KEY_PATIENTS);
            int ratingIndex = res.getColumnIndex(DoctorsHelperParams.KEY_RATING);

            while (res.moveToNext()) {
                // Use column indices to get data
                String name = res.getString(nameIndex);
                String specialization = res.getString(specialityIndex);
                String college = res.getString(collegeIndex);
                String experience = res.getString(experienceIndex);
                String patients = res.getString(patientsIndex);
                String rating = res.getString(ratingIndex);

                // Handle image column
                Uri imageUri = getImageUriFromInternalStorage(context,name);



                if (imageUri != null) {
                    Doctors doctor = new Doctors(imageUri.toString(), name, specialization, college, patients, rating, experience);
                    doctorData.add(doctor);
                    UserData.setDoctorData(doctor);
                }else{
                    Log.e("populateDoctors", "Image URI is null for doctor: " + name);
                    Doctors doctor = new Doctors( name, specialization, college, patients, rating, experience);
                    doctorData.add(doctor);
                    UserData.setDoctorData(doctor);
                }
            }

        } catch (Exception e) {
            Log.e("populateDoctors", "Exception while populating doctors: " + e.getMessage());
            Toast.makeText(context, "Error processing doctor data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (res != null) {
                res.close();
            }
        }

        Log.d("populateDoctors", "Total doctors populated: " + doctorData.size());
        // Optional: Pass doctorData to the UI or other components
    }




    public static Uri getImageUriFromInternalStorage(Context context, String imageName) {
        // Append the PNG extension to the image name
        String fileExtension = ".png";

        // Get the file from internal storage
        File file = new File(context.getFilesDir(), imageName);

        if (file.exists()) {
            // Convert the file to a Uri
            return Uri.fromFile(file);
        } else {
            Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static void setDoctorData(Doctors docData) {
        if(docData != null){
            UserData.doctorData.add(docData);
        }
    }

    public static int getDrawableAvatar() {
        return drawableAvatar;
    }

    public static void setDrawableAvatar(int drawableAvatar) {
        UserData.drawableAvatar = drawableAvatar;
    }

    public static String getAvatar() {
        return Avatar;
    }

    public static void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public UserData() {
    }
    public UserData(String email , String passsword , String username){
        this.email = email;
        this.passsword = passsword;
        this.Username = username;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserData.email = email;
    }

    public static String getPasssword() {
        return passsword;
    }

    public static void setPasssword(String passsword) {
        UserData.passsword = passsword;
    }

    public static String getUsername() {
        return Username;
    }

    public static void setUsername(String username) {
        Username = username;
    }
    
}
