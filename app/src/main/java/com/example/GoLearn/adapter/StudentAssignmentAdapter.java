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
import com.example.GoLearn.model.StudentAssignmentItem;
import com.example.GoLearn.AssignmentDetailActivity;

import java.util.List;

public class StudentAssignmentAdapter extends RecyclerView.Adapter<StudentAssignmentAdapter.StudentViewHolder> {

    private Context context;
    private List<StudentAssignmentItem> studentAssignmentList;

    public StudentAssignmentAdapter(Context context, List<StudentAssignmentItem> studentAssignmentList) {
        this.context = context;
        this.studentAssignmentList = studentAssignmentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentAssignmentItem item = studentAssignmentList.get(position);

        holder.title.setText(item.getTitle());
        holder.dueDate.setText("Due: " + item.getDueDate());
        holder.points.setText("Points: " + item.getPoints());
        holder.status.setText(item.isSubmitted() ? "Submitted" : "Pending");
        holder.obtainedPoints.setText("Obtained Points: " + item.getObtainedPoints());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("dueDate", item.getDueDate());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("points", item.getPoints());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentAssignmentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, points, status, obtainedPoints;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignment_title);
            dueDate = itemView.findViewById(R.id.assignment_due);
            points = itemView.findViewById(R.id.assignment_points);
            status = itemView.findViewById(R.id.assignment_status);
            obtainedPoints = itemView.findViewById(R.id.assignment_obtained_points);
        }
    }
}