package com.example.mad_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.*;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<AssignmentItem> assignmentList;

    public AssignmentAdapter(List<AssignmentItem> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem model = assignmentList.get(position);
        holder.title.setText(model.getTitle());
        holder.due.setText("Due: " + model.getDueDate());
        holder.status.setText(model.getStatus());
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView title, due, status;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignment_title);
            due = itemView.findViewById(R.id.assignment_due);
            status = itemView.findViewById(R.id.assignment_status);
        }
    }
}
