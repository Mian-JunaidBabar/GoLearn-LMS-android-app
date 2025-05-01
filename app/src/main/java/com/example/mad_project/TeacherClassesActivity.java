package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.adapter.ClassAdapter;
import com.example.mad_project.model.ClassItem;

import java.util.ArrayList;

public class TeacherClassesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private ArrayList<ClassItem> teacherClassList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerTeacherClasses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample runtime list for now
        teacherClassList = new ArrayList<>();
        teacherClassList.add(new ClassItem("class01", "AI Advanced", "Deep Learning Project", R.drawable.ic_class, "Active"));
        teacherClassList.add(new ClassItem("class02", "Mobile Dev", "Jetpack Compose App", R.drawable.ic_class, "Completed"));

        classAdapter = new ClassAdapter(this, teacherClassList, classItem -> {
            Intent intent = new Intent(this, ManageClassActivity.class);
            intent.putExtra("classId", classItem.getId());
            intent.putExtra("classTitle", classItem.getTitle());
            intent.putExtra("classDesc", classItem.getDescription());
            intent.putExtra("classIcon", classItem.getIconResId());
            intent.putExtra("classStatus", classItem.getStatus());
            startActivity(intent);
        });

        recyclerView.setAdapter(classAdapter);
    }
}
