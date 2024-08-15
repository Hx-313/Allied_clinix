package com.example.clininallied;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clininallied.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;


public class Home_Fragment extends Fragment {
    RecyclerView Container;
    List<Doctors> doctorsData;
    DoctorsAdapter adapter;

    TextView username;

    public Home_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home_, container, false);
        Container = view.findViewById(R.id.RecyclerView);
        doctorsData = new ArrayList<>();
        Doctors doctor1 = new Doctors(R.drawable.banner_image, "Dr. Abdullah",
                "Medical specialist","king Edward Medical College, MBBS",
                "1000","4.5","4.4 Years");
        doctorsData.add(doctor1);
        Doctors doctor2 = new Doctors(R.drawable.doc_male, "Dr. Zeeshan",
                "Medical specialist","king Edward Medical College, MBBS",
                "2000","4.5","3 Years");
        adapter = new DoctorsAdapter(doctorsData);
        doctorsData.add(doctor2);
        Container.setLayoutManager(new LinearLayoutManager(getContext()));
        Container.setAdapter(adapter);
        username = view.findViewById(R.id.username);
        username.setText( UserData.getUsername());
        return view;
    }
}