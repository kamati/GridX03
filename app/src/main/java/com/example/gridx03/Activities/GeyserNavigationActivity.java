package com.example.gridx03.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.gridx03.Fragments.FragmentManual;
import com.example.gridx03.Fragments.FragmentSchedule;
import com.example.gridx03.Fragments.FragmentTimer;
import com.example.gridx03.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GeyserNavigationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geyser_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_geyser);
        bottomNav.setOnNavigationItemSelectedListener(navLister);
       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_geyser, new FragmentManual()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navLister =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_geyser_manual:
                            selectedFragment = new FragmentManual();
                            break;

                        case R.id.nav_geyser_schedule:

                            selectedFragment = new FragmentSchedule();
                            break;

                        case R.id.nav_geyser_timer:
                            selectedFragment = new FragmentTimer();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_geyser,
                            selectedFragment).commit();
                    return true;
                }
            };

}