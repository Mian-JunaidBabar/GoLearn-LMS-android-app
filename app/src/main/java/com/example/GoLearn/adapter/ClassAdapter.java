package com.example.GoLearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.model.ClassItem;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ClassItem classItem);
    }

    private Context context;
    private List<ClassItem> classList;
    private OnItemClickListener listener;

    public ClassAdapter(Context context, List<ClassItem> classList, OnItemClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem item = classList.get(position);

        // Bind data to views
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.teacher.setText("By " + item.getTeacherName());
        holder.assignments.setText(String.valueOf(item.getStatus()));
        holder.image.setImageResource(item.getIconResId());
        holder.classCode.setText("Code: " + item.getCode());

        // Handle assignment status and obtained points
        if (item.isStudentAssignment()) {
            holder.submissionStatus.setVisibility(View.VISIBLE);
            holder.submissionStatus.setText(item.isSubmitted() ? "Submitted" : "Not Submitted");

            holder.obtainedPoints.setVisibility(View.VISIBLE);
            holder.obtainedPoints.setText("Points: " + item.getObtainedPoints());
        } else {
            holder.submissionStatus.setVisibility(View.GONE);
            holder.obtainedPoints.setVisibility(View.GONE);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, teacher, assignments, submissionStatus, obtainedPoints, classCode;
        ImageView image;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            title = itemView.findViewById(R.id.class_title);
            description = itemView.findViewById(R.id.class_description);
            teacher = itemView.findViewById(R.id.class_teacher);
            assignments = itemView.findViewById(R.id.class_assignments);
            image = itemView.findViewById(R.id.class_image);
            submissionStatus = itemView.findViewById(R.id.assignment_status);
            obtainedPoints = itemView.findViewById(R.id.assignment_obtained_points);
            classCode = itemView.findViewById(R.id.class_code_label);
        }
    }
}