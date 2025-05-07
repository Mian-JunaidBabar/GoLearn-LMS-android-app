package com.example.GoLearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClassActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve classId from Intent
        String classId = getIntent().getStringExtra("classId");
        if (classId == null || classId.isEmpty()) {
            Toast.makeText(this, "Class ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // End the activity to prevent further errors
            return;
        }

        // Use classId to reference the database
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(classId);

        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classTitle = snapshot.child("title").getValue(String.class);
                setTitle(classTitle != null ? classTitle : "Class Title Not Found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setTitle("Error Loading Title");
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            Bundle bundle = new Bundle();
            bundle.putString("classId", classId);

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
                selected.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        // Load default fragment if no savedInstanceState
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
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
        if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (item.getItemId() == R.id.nav_teacher_classes) {
            startActivity(new Intent(this, ManageClassActivity.class));
        } else if (item.getItemId() == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
