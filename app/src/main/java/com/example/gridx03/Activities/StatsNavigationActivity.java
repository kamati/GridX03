package com.example.gridx03.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.gridx03.Fragments.FragmentManual;
import com.example.gridx03.Fragments.FragmentSchedule;
import com.example.gridx03.Fragments.FragmentStatDaily;
import com.example.gridx03.Fragments.FragmentStatHourly;
import com.example.gridx03.Fragments.FragmentStatMonthly;
import com.example.gridx03.Fragments.FragmentTimer;
import com.example.gridx03.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class StatsNavigationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_nagivation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_stat);
        bottomNav.setOnNavigationItemSelectedListener(navLister);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_stat, new FragmentStatDaily()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navLister =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_live:
                            selectedFragment = new FragmentStatHourly();
                            break;

                        case R.id.nav_daily:

                            selectedFragment = new FragmentStatDaily();
                            break;

                        case R.id.nav_monthly:
                            selectedFragment = new FragmentStatMonthly();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_stat,
                            selectedFragment).commit();
                    return true;
                }
            };

}
