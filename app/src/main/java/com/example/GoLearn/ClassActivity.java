package com.example.GoLearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.GoLearn.fragment.ClassAssignmentsFragment;
import com.example.GoLearn.fragment.ClassHomeFragment;
import com.example.GoLearn.fragment.ClassPeopleFragment;
import com.example.GoLearn.fragment.CommentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.example.GoLearn.util.ClassDataHolder;
import com.google.firebase.auth.FirebaseAuth;

public class ClassActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

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

        // Set up DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
                selected = new CommentFragment();
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
            }
            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClassHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            // Navigate to DashboardActivity
            startActivity(new Intent(this, DashboardActivity.class));
        } else if (item.getItemId() == R.id.nav_teacher_classes) {
            // Navigate to ManageClassActivity
            startActivity(new Intent(this, ManageClassActivity.class));
        } else if (item.getItemId() == R.id.nav_profile) {
            // Navigate to ManageClassActivity
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (item.getItemId() == R.id.nav_logout) {
            // Handle logout
            FirebaseAuth.getInstance().signOut(); // Sign out the user
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
            startActivity(intent);
            finish(); // Close the current activity
        } else {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_assignments) {
                selectedFragment = new ClassAssignmentsFragment();
            } else if (item.getItemId() == R.id.nav_people) {
                selectedFragment = new ClassPeopleFragment();
            } else if (item.getItemId() == R.id.nav_comments) {
                selectedFragment = new CommentFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}