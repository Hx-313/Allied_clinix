package com.example.clininallied;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentKt;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    BottomNavigationView bnview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new Home_Fragment())
                    .commit();
        }
        bnview = findViewById(R.id.bottomNavigation);
        bnview.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.navigation_home){
                        addFragment(new Home_Fragment());
                        return true;
                }else if( id == R.id.navigation_notifications){
                        addFragment(new Notification_Fragment());
                        return true;
                }else if (id == R.id.navigation_profile){//profile
                        addFragment(new Profile_Fragment());
                        return true;
                }else{
                    return false;
                }



            }
        });
    }
    public  void addFragment(Fragment frag){
        FragmentManager mgr = getSupportFragmentManager();
        FragmentTransaction ft = mgr.beginTransaction();
        ft.replace(R.id.container,frag);
        ft.commit();

    }
}
