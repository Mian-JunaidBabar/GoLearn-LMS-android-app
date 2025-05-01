package com.example.mad_project;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mad_project.fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.mad_project.fragment.*;


public class ManageClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class);

        String classTitle = getIntent().getStringExtra("classTitle");

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.manageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(classTitle);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Load default fragment
        loadFragment(new ManageHomeFragment());

        // Set up item selection listener
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            if (item.getItemId() == R.id.nav_manage_home) {
                selected = new ManageHomeFragment();
            } else if (item.getItemId() == R.id.nav_manage_people) {
                selected = new ManagePeopleFragment();
            } else if (item.getItemId() == R.id.nav_manage_chat) {
                selected = new CommentFragment();
            }

            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.manageFragmentContainer, fragment)
                .commit();
    }
}