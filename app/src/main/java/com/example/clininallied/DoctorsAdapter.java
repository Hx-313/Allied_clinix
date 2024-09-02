package com.example.clininallied;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<Doctors> doctorsData;
    public DoctorsAdapter(@NonNull List<Doctors> doctorsData) {
        this.doctorsData = doctorsData;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctors doctors = doctorsData.get(position);
        holder.getImage().setImageURI(Uri.parse(doctors.getImage()));
        holder.getName().setText(doctors.getName());
        holder.getSpeciality().setText(doctors.getSpeciality());
        holder.getCollege().setText(doctors.getCollege());
        holder.getPatients().setText(doctors.getPatients());
        holder.getRating().setText(doctors.getRatings());
        holder.getExperience().setText(doctors.getExperience());
    }

    @Override
    public int getItemCount() {
        return doctorsData.size();
    }
}
