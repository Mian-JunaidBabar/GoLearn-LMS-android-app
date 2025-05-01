package com.example.mad_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.ClassActivity;
import com.example.mad_project.CreateClassActivity;
import com.example.mad_project.R;
import com.example.mad_project.adapter.ClassAdapter;
import com.example.mad_project.model.ClassItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final int CREATE_CLASS_REQUEST = 1;
    private List<ClassItem> classList;
    private ClassAdapter classAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize class list and adapter
        classList = new ArrayList<>();
        classList.add(new ClassItem("1", "Mathematics", "Weekly problem-solving class", R.drawable.ic_class, "Mr. Khan"));
        classList.add(new ClassItem("2", "Biology", "Plant cell discussion", R.drawable.ic_class, "Dr. Ahmed"));
        classList.add(new ClassItem("3", "English", "Essay writing tips", R.drawable.ic_class, "Ms. Sara"));
        classList.add(new ClassItem("4", "Physics", "Quantum mechanics overview", R.drawable.ic_class, "Dr. Smith"));
        classList.add(new ClassItem("5", "Chemistry", "Organic chemistry basics", R.drawable.ic_class, "Ms. Johnson"));
        classList.add(new ClassItem("6", "History", "World War II analysis", R.drawable.ic_class, "Mr. Brown"));

        classAdapter = new ClassAdapter(getContext(), classList, classItem -> {
            // Start the ManageClassActivity with the selected class details
            Intent intent = new Intent(getContext(), ClassActivity.class);
            intent.putExtra("classId", classItem.getId());
            intent.putExtra("classTitle", classItem.getTitle());
            intent.putExtra("classDesc", classItem.getDescription());
            intent.putExtra("classIcon", classItem.getIconResId());
            intent.putExtra("classTeacher", classItem.getTeacherName());
            startActivity(intent);
        });
        recyclerView.setAdapter(classAdapter);

        // Initialize FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab_add_class);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Action")
                    .setItems(new CharSequence[]{"Join Class", "Create Class"}, (dialog, which) -> {
                        if (which == 0) {
                            Toast.makeText(requireContext(), "Join Class clicked", Toast.LENGTH_SHORT).show();
                            // TODO: Add JoinClassActivity intent here later
                        } else if (which == 1) {
                            Intent intent = new Intent(getActivity(), CreateClassActivity.class);
                            startActivityForResult(intent, CREATE_CLASS_REQUEST);
                        }
                    })
                    .show();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_CLASS_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            String id = data.getStringExtra("id");
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            int iconResId = data.getIntExtra("iconResId", R.drawable.ic_class);
            String status = data.getStringExtra("status");

            if (id != null && title != null && description != null && status != null) {
                ClassItem newClass = new ClassItem(id, title, description, iconResId, status);
                classList.add(newClass);
                classAdapter.notifyItemInserted(classList.size() - 1);
            }
        }
    }
}