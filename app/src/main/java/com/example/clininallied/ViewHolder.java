package com.example.clininallied;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView image;
    private TextView name;
    private TextView speciality;
    private TextView college;
    private TextView experience;
    private TextView patients;
    private TextView rating;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image_view);
        name = itemView.findViewById(R.id.name);
        speciality = itemView.findViewById(R.id.speciality);
        college = itemView.findViewById(R.id.education);
        patients = itemView.findViewById(R.id.patients);
        rating = itemView.findViewById(R.id.rating);
        experience = itemView.findViewById(R.id.experience);
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getRating() {
        return rating;
    }

    public TextView getPatients() {
        return patients;
    }

    public TextView getExperience() {
        return experience;
    }

    public TextView getCollege() {
        return college;
    }

    public TextView getSpeciality() {
        return speciality;
    }

    public TextView getName() {
        return name;
    }


}
