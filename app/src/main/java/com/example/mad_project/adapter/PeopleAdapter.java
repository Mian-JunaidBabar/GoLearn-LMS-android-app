package com.example.mad_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.PersonItem;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private List<PersonItem> personList;

    public PeopleAdapter(List<PersonItem> personList) {
        this.personList = personList;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position) {
        PersonItem person = personList.get(position);
        holder.name.setText(person.getName());
        holder.role.setText(person.getRole());
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public static class PeopleViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;

        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.person_name);
            role = itemView.findViewById(R.id.person_role);
        }
    }
}
