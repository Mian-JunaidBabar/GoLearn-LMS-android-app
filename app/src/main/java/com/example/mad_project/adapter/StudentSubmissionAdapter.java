package com.example.mad_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.model.StudentSubmissionItem;

import java.util.List;

public class StudentSubmissionAdapter extends RecyclerView.Adapter<StudentSubmissionAdapter.ViewHolder> {

    private final List<StudentSubmissionItem> submissionsList;

    public StudentSubmissionAdapter(List<StudentSubmissionItem> submissionsList) {
        this.submissionsList = submissionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentSubmissionItem item = submissionsList.get(position);
        holder.studentName.setText(item.getStudentName());
        holder.submissionDate.setText(item.getSubmissionDate());
        holder.fileName.setText(item.getFileName());
    }

    @Override
    public int getItemCount() {
        return submissionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, submissionDate, fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.student_name);
            submissionDate = itemView.findViewById(R.id.submission_date);
            fileName = itemView.findViewById(R.id.file_name);
        }
    }
}