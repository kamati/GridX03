package com.example.gridx03.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.gridx03.Fragments.FragmentBuyToken;
import com.example.gridx03.Fragments.FragmentManual;
import com.example.gridx03.Fragments.FragmentSchedule;
import com.example.gridx03.Fragments.FragmentSendToken;
import com.example.gridx03.Fragments.FragmentTimer;
import com.example.gridx03.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class RechargeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_navigation);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_recharge);
        bottomNav.setOnNavigationItemSelectedListener(navLister);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_recharge, new FragmentSendToken()).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navLister =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_recharge_buy:
                            selectedFragment = new FragmentBuyToken();
                            break;

                        case R.id.nav_recharge_send:
                            selectedFragment = new FragmentSendToken();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_recharge,
                            selectedFragment).commit();
                    return true;
                }
            };
}
