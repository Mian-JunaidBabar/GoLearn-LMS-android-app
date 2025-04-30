package com.example.mad_project.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mad_project.R;
import com.example.mad_project.adapter.PeopleAdapter;
import com.example.mad_project.model.PersonItem;

import java.util.ArrayList;
import java.util.List;

public class ClassPeopleFragment extends Fragment {

    private RecyclerView recyclerView;
    private PeopleAdapter adapter;
    private List<PersonItem> peopleList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        recyclerView = view.findViewById(R.id.people_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Static data
        peopleList = new ArrayList<>();
        peopleList.add(new PersonItem("Mr. Ahmed Khan", "Teacher"));
        peopleList.add(new PersonItem("Ali Raza", "Student"));
        peopleList.add(new PersonItem("Sara Malik", "Student"));
        peopleList.add(new PersonItem("Hassan Javed", "Student"));

        adapter = new PeopleAdapter(peopleList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
