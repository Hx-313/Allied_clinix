package com.example.clininallied;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class Profile_Fragment extends Fragment {

    Button btn_profile;
    TextView username;
    TextView email;

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


        return view;
    }
}