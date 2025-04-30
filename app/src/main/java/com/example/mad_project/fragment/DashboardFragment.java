package com.example.mad_project.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.adapter.ClassAdapter;
import com.example.mad_project.model.ClassItem;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ClassItem> classList = new ArrayList<>();
        classList.add(new ClassItem("1", "Mathematics", "Weekly problem-solving class", R.drawable.ic_class, "Mr. Khan"));
        classList.add(new ClassItem("2", "Biology", "Plant cell discussion", R.drawable.ic_class, "Dr. Ahmed"));
        classList.add(new ClassItem("3", "English", "Essay writing tips", R.drawable.ic_class, "Ms. Sara"));
        classList.add(new ClassItem("4", "Physics", "Quantum mechanics overview", R.drawable.ic_class, "Dr. Smith"));
        classList.add(new ClassItem("5", "Chemistry", "Organic chemistry basics", R.drawable.ic_class, "Ms. Johnson"));
        classList.add(new ClassItem("6", "History", "World War II analysis", R.drawable.ic_class, "Mr. Brown"));

        ClassAdapter adapter = new ClassAdapter(getContext(), classList);
        recyclerView.setAdapter(adapter);

        // Initialize Floating Action Button and set its click listener
        View fab = view.findViewById(R.id.fab_add_class);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose an option")
                    .setItems(new CharSequence[]{"Create Class", "Join Class"}, (dialog, which) -> {
                        if (which == 0) {
                            // Handle create class
                        } else {
                            // Handle join class
                        }
                    }).show();
        });

        return view;
    }
}