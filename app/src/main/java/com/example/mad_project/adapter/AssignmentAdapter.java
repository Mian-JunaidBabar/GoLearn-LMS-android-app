package com.example.mad_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.AssignmentItem;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private Context context;
    private List<AssignmentItem> assignmentList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AssignmentItem item);
    }

    public AssignmentAdapter(Context context, List<AssignmentItem> assignmentList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem assignment = assignmentList.get(position);

        // Ensure all TextViews are properly set
        if (holder.title != null) {
            holder.title.setText(assignment.getTitle());
        }
        if (holder.dueDate != null) {
            holder.dueDate.setText("Due: " + assignment.getDueDate());
        }
        if (holder.points != null) {
            holder.points.setText("Points: " + assignment.getPoints());
        }
        if (holder.description != null) {
            holder.description.setText(assignment.getDescription());
        }

        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(assignment);
            Toast.makeText(context, "Clicked: " + assignment.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, points, description;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignment_title);
            dueDate = itemView.findViewById(R.id.assignment_due);
            points = itemView.findViewById(R.id.assignment_points);
            description = itemView.findViewById(R.id.assignment_description);
        }
    }
}