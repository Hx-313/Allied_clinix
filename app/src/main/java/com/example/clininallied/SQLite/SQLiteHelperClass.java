package com.example.clininallied.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.clininallied.BitmapCallback;
import com.example.clininallied.Doctors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SQLiteHelperClass extends SQLiteOpenHelper {
    Context context;
    public SQLiteHelperClass( Context context) {

        super(context,DoctorsHelperParams.DATABASE_NAME,
                null, DoctorsHelperParams.DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DOCTORS_TABLE = "CREATE TABLE " + DoctorsHelperParams.TABLE_NAME + "("
                + DoctorsHelperParams.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DoctorsHelperParams.KEY_NAME + " TEXT, "
                + DoctorsHelperParams.KEY_SPECIALITY + " TEXT, "
                + DoctorsHelperParams.KEY_COLLEGE + " TEXT, "
                + DoctorsHelperParams.KEY_EXPERIENCE + " TEXT, "
                + DoctorsHelperParams.KEY_PATIENTS + " INTEGER, "
                + DoctorsHelperParams.KEY_RATING + " REAL, "
                + DoctorsHelperParams.KEY_IMAGE + " BLOB)";  // Add BLOB column for image

        Log.d("SQLITE", CREATE_DOCTORS_TABLE);
        db.execSQL(CREATE_DOCTORS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DoctorsHelperParams.TABLE_NAME);
        onCreate(db);
    }
    public void addNewDoctor(Doctors doctor, boolean remote) {
        if (doctor.getImage() != null) {
            Log.d("addNewDoctor", "Image URI: " + doctor.getImage());
            Uri uri = Uri.parse(doctor.getImage());

            if (remote) {
                // Process remote image asynchronously
                getBitmapFromRemoteUri(uri, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        try {
                             Uri ImageUri = saveImageToInternalStorage(bitmap,doctor.getName());
                            Log.d("addNewDoctor", "Image saved to internal storage." + ImageUri);
                            insertDoctorIntoDatabase(doctor);
                        } catch (Exception e) {
                            Log.e("addNewDoctor", "Error converting bitmap to byte array: " + e.getMessage());
                            Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onBitmapLoadFailed(Exception e) {
                        Log.e("addNewDoctor", "Error getting bitmap from remote URI: " + e.getMessage());
                        Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Process local image synchronously
                try {
                    Bitmap bitmap = getBitmapFromLocalUri(uri);
                    saveImageToInternalStorage(bitmap,doctor.getName());
                    insertDoctorIntoDatabase(doctor);
                } catch (Exception e) {
                    Log.e("addNewDoctor", "Error getting bitmap from local URI: " + e.getMessage());
                    Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.e("addNewDoctor", "Image URI is null.");
            Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
        }
    }
    public Uri saveImageToInternalStorage(Bitmap bitmap, String imageName) {
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


    private void insertDoctorIntoDatabase(Doctors doctor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DoctorsHelperParams.KEY_NAME, doctor.getName());
        values.put(DoctorsHelperParams.KEY_SPECIALITY, doctor.getSpeciality());
        values.put(DoctorsHelperParams.KEY_COLLEGE, doctor.getCollege());
        values.put(DoctorsHelperParams.KEY_EXPERIENCE, doctor.getExperience());
        values.put(DoctorsHelperParams.KEY_PATIENTS, doctor.getPatients());
        values.put(DoctorsHelperParams.KEY_RATING, doctor.getRatings());
        values.put(DoctorsHelperParams.KEY_IMAGE, doctor.getImageToStoreInDB());

        long result = db.insert(DoctorsHelperParams.TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("insertDoctorIntoDatabase", "Failed to insert doctor data into the database.");
            Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("insertDoctorIntoDatabase", "Doctor data inserted with ID: " + result);
            Toast.makeText(context, "Doctor added successfully", Toast.LENGTH_SHORT).show();
        }
    }



    private Bitmap getBitmapFromLocalUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void getBitmapFromRemoteUri(Uri uri, BitmapCallback callback) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        callback.onBitmapReady(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Optionally handle cleanup
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        callback.onBitmapLoadFailed(new Exception("Failed to load bitmap from remote URI"));
                    }
                });
    }






    public Cursor getData ()
    {
    SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("SELECT * FROM " + DoctorsHelperParams.TABLE_NAME, null);
    }

    public void openWriteableDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

    }
    public void openReadableDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
    }
    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
        }
}
