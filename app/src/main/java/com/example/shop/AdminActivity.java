package com.example.shop;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Load the default fragment (HomeFragment)
        replaceFragment(new AdminDashboardFragment());

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Create a map to associate menu item IDs with fragments
        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.profile, new AdminProfileFragment());
        fragmentMap.put(R.id.dasboard, new AdminDashboardFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            return replaceFragment(selectedFragment);
        });
    }

    private boolean replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.admin, fragment); // Ensure this ID is correct
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }
}