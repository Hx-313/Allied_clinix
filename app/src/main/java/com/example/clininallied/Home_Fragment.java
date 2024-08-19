package com.example.clininallied;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.clininallied.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.Shapeable;

import java.util.ArrayList;
import java.util.List;


public class Home_Fragment extends Fragment {
    RecyclerView Container;
    List<Doctors> doctorsData;
    DoctorsAdapter adapter;
    ShapeableImageView avatar;


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
        avatar= view.findViewById(R.id.avatar_image);

        if(UserData.getAvatar() ==null){
            avatar.setImageResource(UserData.getDrawableAvatar());
        }else{
           Glide.with(this).load(UserData.getAvatar()).into(avatar);
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
                FragmentManager mgr = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mgr.beginTransaction();
                ft.replace(R.id.container,new Profile_Fragment());
                ft.commit();
            }
        });
        return view;
    }

    }

