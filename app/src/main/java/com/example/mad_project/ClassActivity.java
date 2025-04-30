package com.example.mad_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mad_project.fragment.ClassAssignmentsFragment;
import com.example.mad_project.fragment.ClassCommentsFragment;
import com.example.mad_project.fragment.ClassHomeFragment;
import com.example.mad_project.fragment.ClassPeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.mad_project.util.*;

public class ClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Retrieve data from Intent
        String classId = getIntent().getStringExtra("CLASS_ID");
        String title = getIntent().getStringExtra("CLASS_TITLE");
        String desc = getIntent().getStringExtra("CLASS_DESCRIPTION");
        String teacher = getIntent().getStringExtra("TEACHER_NAME");

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);

        // Store values in static/global vars for fragments
        ClassDataHolder.classId = classId;
        ClassDataHolder.description = desc;
        ClassDataHolder.teacher = teacher;

        // Set up BottomNavigationView
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