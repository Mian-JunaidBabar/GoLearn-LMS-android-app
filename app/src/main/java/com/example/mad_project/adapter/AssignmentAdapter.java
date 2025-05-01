package com.example.mad_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.AssignmentItem;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(AssignmentItem item);
    }

    private Context context;
    private List<AssignmentItem> assignmentList;
    private OnItemClickListener listener;

    public AssignmentAdapter(Context context, List<AssignmentItem> assignmentList, OnItemClickListener listener) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment_student, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem item = assignmentList.get(position);
        holder.title.setText(item.getTitle());
        holder.dueDate.setText("Due: " + item.getDueDate());
        holder.points.setText("Points: " + item.getPoints());

        if (item instanceof com.example.mad_project.model.StudentAssignmentItem) {
            com.example.mad_project.model.StudentAssignmentItem studentItem = (com.example.mad_project.model.StudentAssignmentItem) item;
            holder.status.setText(studentItem.isSubmitted() ? "Submitted" : "Not Submitted");
            holder.obtainedPoints.setText("Obtained Points: " + studentItem.getObtainedPoints());
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, points, status, obtainedPoints;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignment_title);
            dueDate = itemView.findViewById(R.id.assignment_due);
            points = itemView.findViewById(R.id.assignment_points);
            status = itemView.findViewById(R.id.assignment_status);
            obtainedPoints = itemView.findViewById(R.id.assignment_obtained_points);
        }
    }
}