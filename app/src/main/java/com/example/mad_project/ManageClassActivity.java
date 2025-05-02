package com.example.mad_project;

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

import com.example.mad_project.fragment.CommentFragment;
import com.example.mad_project.fragment.ManageHomeFragment;
import com.example.mad_project.fragment.ManagePeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ManageClassActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class);

        String classTitle = getIntent().getStringExtra("classTitle");

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.manageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(classTitle);

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
            startActivity(new Intent(this, ManageClassActivity.class));
        } else if (item.getItemId() == R.id.nav_logout) {
            // Handle logout
            FirebaseAuth.getInstance().signOut(); // Sign out the user
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
            startActivity(intent);
            finish(); // Close the current activity
        } else {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_manage_home) {
                selectedFragment = new ManageHomeFragment();
            } else if (item.getItemId() == R.id.nav_manage_people) {
                selectedFragment = new ManagePeopleFragment();
            } else if (item.getItemId() == R.id.nav_manage_chat) {
                selectedFragment = new CommentFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}