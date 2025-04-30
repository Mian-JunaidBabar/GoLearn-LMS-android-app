package com.example.mad_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mad_project.R;
import com.example.mad_project.fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClassActivity extends AppCompatActivity {

    public static String CLASS_TITLE = "AI Basics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(CLASS_TITLE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClassHomeFragment()).commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            if (item.getItemId() == R.id.nav_home) {
                selected = new ClassHomeFragment();
            } else if (item.getItemId() == R.id.nav_assignments) {
                selected = new ClassAssignmentsFragment();
            } else if (item.getItemId() == R.id.nav_people) {
                selected = new ClassPeopleFragment();
            } else if (item.getItemId() == R.id.nav_comments) {
                selected = new ClassCommentsFragment();
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
            }
            return true;
        });
    }
}
