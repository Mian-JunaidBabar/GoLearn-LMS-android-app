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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

        // Convert dueDate string to a formatted date
        try {
            long dueDateMillis = item.getDueDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(dueDateMillis);
            holder.dueDate.setText("Due: " + formattedDate);
        } catch (NumberFormatException e) {
            holder.dueDate.setText("Due: Invalid Date");
        }

        holder.points.setText("Points: " + item.getPoints());
        holder.status.setText(item.isSubmitted() ? "Submitted" : "Pending");

        if (item.isSubmitted()) {
            if (item.getObtainedPoints() >= 0) {
                holder.obtainedPoints.setText("Obtained Points: " + item.getObtainedPoints());
            } else {
                holder.obtainedPoints.setText("Obtained Points: Not graded yet");
            }
        } else {
            holder.obtainedPoints.setText("Obtained Points: --");
        }

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