package com.example.GoLearn.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.PeopleAdapter;
import com.example.GoLearn.model.PersonItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassPeopleFragment extends Fragment {

    private RecyclerView recyclerView;
    private PeopleAdapter adapter;
    private List<PersonItem> peopleList;
    private String classId;
    private String currentUserId;
    private DatabaseReference classMembersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        recyclerView = view.findViewById(R.id.people_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        peopleList = new ArrayList<>();
        adapter = new PeopleAdapter(peopleList);
        recyclerView.setAdapter(adapter);

        classId = getArguments() != null ? getArguments().getString("classId") : null;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (classId == null) {
            Toast.makeText(getContext(), "Class ID not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        classMembersRef = FirebaseDatabase.getInstance().getReference("classes")
                .child(classId)
                .child("members");

        fetchClassMembers();

        return view;
    }

    private void fetchClassMembers() {
        classMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PersonItem teacher = null;
                List<PersonItem> others = new ArrayList<>();

                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    String uid = memberSnapshot.getKey();
                    String name = memberSnapshot.child("name").getValue(String.class);
                    String role = memberSnapshot.child("role").getValue(String.class);
                    Long joinedAt = memberSnapshot.child("joinedAt").getValue(Long.class);

                    if (uid == null || name == null || role == null || joinedAt == null) continue;

                    PersonItem person = new PersonItem(uid, name, role, joinedAt);

                    if (role.equalsIgnoreCase("teacher")) {
                        teacher = person;
                    } else {
                        others.add(person);
                    }
                }

                peopleList.clear();
                if (teacher != null) peopleList.add(teacher);
                peopleList.addAll(others);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load members: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
