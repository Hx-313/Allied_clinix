package com.example.clininallied.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.clininallied.Doctors;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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
    public void addNewDoctor(Doctors doctor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DoctorsHelperParams.KEY_NAME, doctor.getName());
        values.put(DoctorsHelperParams.KEY_SPECIALITY, doctor.getSpeciality());
        values.put(DoctorsHelperParams.KEY_COLLEGE, doctor.getCollege());
        values.put(DoctorsHelperParams.KEY_EXPERIENCE, doctor.getExperience());
        values.put(DoctorsHelperParams.KEY_PATIENTS, doctor.getPatients());
        values.put(DoctorsHelperParams.KEY_RATING, doctor.getRatings());
        if(doctor.getImage() != null){
            Log.d("addNewDoctor", "Image URI: " + doctor.getImage());
            Uri uri = Uri.parse(doctor.getImage());
            Bitmap bitmap = null;
            try {
                bitmap = getBitmapFromUri(uri);
            } catch (Exception e) {
                Log.e("addNewDoctor", "Error getting bitmap from URI: " + e.getMessage());
                return; // Exit method if bitmap retrieval fails
            }
            byte[] byteArray = convertBitmapToByteArray(bitmap);
            doctor.setImageToSotreInDB(byteArray);

            values.put(DoctorsHelperParams.KEY_IMAGE, doctor.getImageToStoreInDB());
            long result = db.insert(DoctorsHelperParams.TABLE_NAME, null, values);
            if (result == -1) {
                Log.e("addNewDoctor", "Failed to insert doctor data into the database.");
                Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("addNewDoctor", "Doctor data inserted with ID: " + result);
                Toast.makeText(context, "Doctor added successfully", Toast.LENGTH_SHORT).show();

            }
        }else {
            Log.e("addNewDoctor", "Image URI is null.");
            Toast.makeText(context, "Failed to add doctor", Toast.LENGTH_SHORT).show();
        }






    }


    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Compress to PNG format
        return outputStream.toByteArray();
    }


    public Cursor getData ()
    {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM " + DoctorsHelperParams.TABLE_NAME, null);
        return cursor;
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
