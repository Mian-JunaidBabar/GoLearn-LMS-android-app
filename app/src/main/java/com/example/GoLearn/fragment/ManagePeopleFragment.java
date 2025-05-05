package com.example.GoLearn.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.StudentAdapter;
import com.example.GoLearn.model.PersonItem;

import java.util.ArrayList;
import java.util.List;

public class ManagePeopleFragment extends Fragment {

    private List<PersonItem> studentList;
    private StudentAdapter adapter;
    private TextView teacherName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_people, container, false);

        teacherName = view.findViewById(R.id.teacherName);
        teacherName.setText("Prof. Muhammad Junaid");

        RecyclerView recyclerView = view.findViewById(R.id.studentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        studentList = new ArrayList<>();
        studentList.add(new PersonItem("stu01", "Ali"));
        studentList.add(new PersonItem("stu02", "Sara"));

        adapter = new StudentAdapter(studentList, student -> {
            studentList.remove(student);
            adapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addStudentButton).setOnClickListener(v -> {
            // Temporary addition logic
            String newId = "stu" + (studentList.size() + 1);
            studentList.add(new PersonItem(newId, "New Student " + newId));
            adapter.notifyItemInserted(studentList.size() - 1);
        });

        return view;
    }
}
