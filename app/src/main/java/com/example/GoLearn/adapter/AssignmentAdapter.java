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
import com.example.GoLearn.model.AssignmentItem;
import com.example.GoLearn.AssignmentSubmissionsActivity;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.TeacherViewHolder> {

    private Context context;
    private List<AssignmentItem> assignmentList;

    public AssignmentAdapter(Context context, List<AssignmentItem> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        AssignmentItem item = assignmentList.get(position);

        holder.title.setText(item.getTitle());
        holder.dueDate.setText("Due: " + item.getDueDate());
        holder.points.setText("Points: " + item.getPoints());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentSubmissionsActivity.class);
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

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, points;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignment_title);
            dueDate = itemView.findViewById(R.id.assignment_due);
            points = itemView.findViewById(R.id.assignment_points);
        }
    }
}