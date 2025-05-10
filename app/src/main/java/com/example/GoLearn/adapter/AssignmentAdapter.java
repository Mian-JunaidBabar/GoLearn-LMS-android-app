package com.example.GoLearn.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.AssignmentSubmissionsActivity;
import com.example.GoLearn.model.AssignmentItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private Context context;
    private List<AssignmentItem> assignmentList;

    public AssignmentAdapter(Context context, List<AssignmentItem> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem item = assignmentList.get(position);

        holder.title.setText(item.getTitle());
        holder.dueDate.setText("Due: " + formatDate(item.getDueDate()));
        holder.points.setText("Points: " + item.getPoints());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentSubmissionsActivity.class);
            intent.putExtra("classId", item.getClassId());
            intent.putExtra("assignmentId", item.getAssignmentId());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("dueDate", item.getDueDate());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("points", item.getPoints());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(timestamp);
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, points;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignment_title);
            dueDate = itemView.findViewById(R.id.assignment_due);
            points = itemView.findViewById(R.id.assignment_points);
        }
    }
}