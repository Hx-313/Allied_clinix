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
        Cursor res = db.getData();

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

        try {
            // Get column indices by column names
            int nameIndex = res.getColumnIndex(DoctorsHelperParams.KEY_NAME);
            int specialityIndex = res.getColumnIndex(DoctorsHelperParams.KEY_SPECIALITY);
            int collegeIndex = res.getColumnIndex(DoctorsHelperParams.KEY_COLLEGE);
            int experienceIndex = res.getColumnIndex(DoctorsHelperParams.KEY_EXPERIENCE);
            int patientsIndex = res.getColumnIndex(DoctorsHelperParams.KEY_PATIENTS);
            int ratingIndex = res.getColumnIndex(DoctorsHelperParams.KEY_RATING);
            int imageIndex = res.getColumnIndex(DoctorsHelperParams.KEY_IMAGE);

            while (res.moveToNext()) {
                // Use column indices to get data
                String name = res.getString(nameIndex);
                String specialization = res.getString(specialityIndex);
                String college = res.getString(collegeIndex);
                String experience = res.getString(experienceIndex);
                String patients = res.getString(patientsIndex);
                String rating = res.getString(ratingIndex);

                // Check if the image column is a BLOB
                if (res.getType(imageIndex) != Cursor.FIELD_TYPE_BLOB) {
                    Log.e("populateDoctors", "Column for image is not of type BLOB");
                    continue;
                }

                byte[] image = res.getBlob(imageIndex);

                // Handle potential issues with image conversion
                String imageName = name + "_image.png";
                File imageFile = new File(context.getFilesDir(), imageName);

                Uri imageUri = null;

                if (imageFile.exists()) {
                    imageUri = Uri.fromFile(imageFile);
                    Log.d("populateDoctors", "Image already exists, using existing image.");
                } else {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    if (bitmap != null) {
                        imageUri = saveBitmapToInternalStorage(context, bitmap, imageName);
                        Log.d("populateDoctors", "Image saved to internal storage.");
                    } else {
                        Log.e("populateDoctors", "Failed to decode image from blob");
                        Toast.makeText(context, "Failed to decode image", Toast.LENGTH_SHORT).show();
                    }
                }

                if (imageUri != null) {
                    Doctors doctor = new Doctors(imageUri.toString(), name, specialization, college, patients, rating, experience);
                    doctorData.add(doctor);
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
    }



    private static Uri saveBitmapToInternalStorage(Context context, Bitmap bitmap, String imageName) {
        FileOutputStream fos = null;
        Uri uri = null;
        try {
            // Save the bitmap to the internal storage directory
            File file = new File(context.getFilesDir(), imageName);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();

            // Get the Uri for the saved file
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return uri;
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
